package io.target365.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.target365.dto.*;
import io.target365.dto.enums.UserValidity;
import io.target365.handler.CreatedResponseParser;
import io.target365.handler.InvalidResponseHandler;
import io.target365.handler.NotFoundResponseParser;
import io.target365.handler.OkResponseParser;
import io.target365.handler.ResponseHandler;
import io.target365.handler.ResponseParser;
import io.target365.service.AuthorizationService;
import io.target365.service.EcdsaAuthorizationService;
import io.target365.service.EcdsaSigner;
import io.target365.service.EcdsaVerifier;
import io.target365.service.JacksonObjectMappingService;
import io.target365.service.Jsr303ValidationService;
import io.target365.service.Jsr303ValidationService.NoBlanksValidator;
import io.target365.service.Jsr303ValidationService.NotBlankValidator;
import io.target365.service.Jsr303ValidationService.NotEmptyValidator;
import io.target365.service.Jsr303ValidationService.NotNullValidator;
import io.target365.service.Jsr303ValidationService.PatternValidator;
import io.target365.service.Jsr303ValidationService.TimestampValidator;
import io.target365.service.Jsr303ValidationService.ValidValidator;
import io.target365.service.ObjectMappingService;
import io.target365.service.Signer;
import io.target365.service.ValidationService;
import io.target365.util.Util;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Target365Client implements Client {

    /**
     * In order to simulate {@link Void} response, we need to return <code>null</code>
     * This variable should be used for that
     */
    private static final Void VOID = null;

    private final Parameters parameters;

    private final OkHttpClient okHttpClient;
    private final ResponseHandler responseHandler;

    private final Signer signer;
    private final AuthorizationService authorizationService;
    private final ObjectMappingService objectMappingService;
    private final ValidationService validationService;

    private final Map<Integer, ResponseParser> responseParsers;

    @Override
    public Future<String> getPing() {
        return doGet("api/ping", Status.OK)
                .thenApplyAsync(response -> Util.wrap(response::body))
                .thenApplyAsync(body -> Util.wrap(body::string));
    }

    @Override
    public Future<List<Keyword>> getKeywords() {
        return getKeywords(null, null, null, null);
    }

    @Override
    public Future<List<Keyword>> getKeywords(
            final String shortNumberId, final String keywordText, final Keyword.Mode mode, final String tag
    ) {
        final List<Param> params = ImmutableList.of(
                new Param("shortNumberId", shortNumberId), new Param("keywordText", keywordText),
                new Param("mode", Optional.ofNullable(mode).map(Keyword.Mode::toString).orElse(null)), new Param("tag", tag)
        );

        return doGet("api/keywords", params, Status.OK)
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, Type.LIST_OF_KEYWORDS));
    }

    @Override
    public Future<String> postKeyword(final Keyword keyword) {
        validationService.validate(NotNullValidator.of("keyword", keyword), ValidValidator.of("keyword", keyword));

        return doPost("api/keywords", objectMappingService.toString(keyword), Status.CREATED)
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response));
    }

    @Override
    public Future<Keyword> getKeyword(final String keywordId) {
        validationService.validate(NotBlankValidator.of("keywordId", keywordId));

        return doGet("api/keywords/" + Util.safeEncode(keywordId), ImmutableList.of(Status.OK, Status.NOT_FOUND))
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, Keyword.class));
    }

    @Override
    public Future<Void> putKeyword(final Keyword keyword) {
        validationService.validate(NotNullValidator.of("keyword", keyword), NotNullValidator.of("keyword.keywordId", keyword.getKeywordId()),
                ValidValidator.of("keyword", keyword));

        return doPut("api/keywords/" + Util.safeEncode(keyword.getKeywordId()), objectMappingService.toString(keyword), Status.NO_CONTENT)
                .thenApplyAsync(response -> VOID);
    }

    @Override
    public Future<Void> deleteKeyword(final String keywordId) {
        validationService.validate(NotBlankValidator.of("keywordId", keywordId));

        return doDelete("api/keywords/" + Util.safeEncode(keywordId), Status.NO_CONTENT)
                .thenApplyAsync(response -> VOID);
    }

    @Override
    public Future<LookupResult> addressLookup(final String msisdn) {
        validationService.validate(NotBlankValidator.of("msisdn", msisdn));

        return doGet("api/lookup", ImmutableList.of(new Param("msisdn", msisdn)), Status.OK)
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, LookupResult.class));
    }

    @Override
    public Future<Void> prepareMsisdns(final List<String> msisdns) {
        validationService.validate(NotEmptyValidator.of("msisdns", msisdns), NoBlanksValidator.of("msisdns", msisdns));

        return doPost("api/prepare-msisdns", objectMappingService.toString(msisdns), Status.NO_CONTENT)
                .thenApplyAsync(response -> VOID);
    }

    @Override
    public Future<List<String>> postOutMessageBatch(final OutMessageBatch outMessageBatch) {
        validationService.validate(NotNullValidator.of("outMessageBatch", outMessageBatch), ValidValidator.of("outMessageBatch", outMessageBatch));

        return doPost("api/out-messages/batch", objectMappingService.toString(outMessageBatch.getItems()), Status.CREATED)
                /*
                 * Normally batch creation of out-messages returns nothing, so we manually create locations
                 * for all created out-messages using transaction ids provided in the request
                 */
                .thenApplyAsync(location -> outMessageBatch.getItems().stream().map(OutMessage::getTransactionId).collect(Collectors.toList()));
    }

    @Override
    public Future<String> postOutMessage(final OutMessage outMessage) {
        validationService.validate(NotNullValidator.of("outMessage", outMessage), ValidValidator.of("outMessage", outMessage));

        return doPost("api/out-messages", objectMappingService.toString(outMessage), Status.CREATED)
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response));
    }

    @Override
    public Future<OutMessage> getOutMessage(final String transactionId) {
        validationService.validate(NotBlankValidator.of("transactionId", transactionId));

        return doGet("api/out-messages/" + Util.safeEncode(transactionId), ImmutableList.of(Status.OK, Status.NOT_FOUND))
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, OutMessage.class));
    }

    @Override
    public Future<Void> putOutMessage(final OutMessage outMessage) {
        validationService.validate(NotNullValidator.of("outMessage", outMessage), ValidValidator.of("outMessage", outMessage),
                NotBlankValidator.of("outMessage.transactionId", outMessage != null ? outMessage.getTransactionId() : null));

        return doPut("api/out-messages/" + Util.safeEncode(outMessage.getTransactionId()), objectMappingService.toString(outMessage), Status.NO_CONTENT)
                .thenApplyAsync(response -> VOID);
    }

    @Override
    public Future<Void> deleteOutMessage(final String transactionId) {
        validationService.validate(NotBlankValidator.of("transactionId", transactionId));

        return doDelete("api/out-messages/" + Util.safeEncode(transactionId), Status.NO_CONTENT)
                .thenApplyAsync(response -> VOID);
    }

    @Override
    public Future<String> getOutMessageExport(final ZonedDateTime from, final ZonedDateTime to) {
        validationService.validate(NotNullValidator.of("from", from), NotNullValidator.of("to", to));

        String path = "api/export/out-messages"
                + "?from=" + Util.safeEncode(from.format(DateTimeFormatter.ISO_INSTANT))
                + "&to=" + Util.safeEncode(to.format(DateTimeFormatter.ISO_INSTANT));

        return doGet(path, ImmutableList.of(Status.OK))
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response));
    }

    @Override
    public Future<InMessage> getInMessage(final String shortNumberId, final String transactionId) {
        validationService.validate(NotBlankValidator.of("shortNumberId", shortNumberId), NotBlankValidator.of("transactionId", transactionId));

        final String url = "api/in-messages/" + Util.safeEncode(shortNumberId) + "/" + Util.safeEncode(transactionId);
        return doGet(url, ImmutableList.of(Status.OK, Status.NOT_FOUND))
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, InMessage.class));
    }

    @Override
    public Future<List<StrexMerchantId>> getMerchantIds() {
        return doGet("api/strex/merchants", Status.OK)
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, Type.LIST_OF_MERCHANTS));
    }

    @Override
    public Future<StrexMerchantId> getMerchantId(final String merchantId) {
        validationService.validate(NotBlankValidator.of("merchantId", merchantId));

        return doGet("api/strex/merchants/" + Util.safeEncode(merchantId), ImmutableList.of(Status.OK, Status.NOT_FOUND))
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, StrexMerchantId.class));
    }

    @Override
    public Future<Void> postStrexOneTimePassword(final StrexOneTimePassword oneTimePassword) {
        validationService.validate(NotNullValidator.of("oneTimePassword", oneTimePassword),
                ValidValidator.of("oneTimePassword", oneTimePassword));

        return doPost("api/strex/one-time-passwords", objectMappingService.toString(oneTimePassword), Status.CREATED)
                .thenApplyAsync(response -> VOID);
    }

    @Override
    public Future<StrexOneTimePassword> getStrexOneTimePassword(final String transactionId) {
        validationService.validate(NotBlankValidator.of("transactionId", transactionId));

        return doGet("api/strex/one-time-passwords/" + Util.safeEncode(transactionId), ImmutableList.of(Status.OK, Status.NOT_FOUND))
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, StrexOneTimePassword.class));
    }

    @Override
    public Future<Void> postStrexTransaction(final StrexTransaction transaction) {
        validationService.validate(NotNullValidator.of("transaction", transaction),
                ValidValidator.of("transaction", transaction));

        return doPost("api/strex/transactions", objectMappingService.toString(transaction), Status.CREATED)
                .thenApplyAsync(response -> VOID);
    }

    @Override
    public Future<StrexTransaction> getStrexTransaction(final String transactionId) {
        validationService.validate(NotBlankValidator.of("transactionId", transactionId));

        return doGet("api/strex/transactions/" + Util.safeEncode(transactionId), ImmutableList.of(Status.OK, Status.NOT_FOUND))
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, StrexTransaction.class));
    }

    @Override
    public Future<Void> saveOneClickConfig(final OneClickConfig config) {
        validationService.validate(NotNullValidator.of("config", config),
                ValidValidator.of("config", config));

        return doPut("api/one-click/configs/" + Util.safeEncode(config.getConfigId()), objectMappingService.toString(config), Status.CREATED)
                .thenApplyAsync(response -> VOID);
    }

    @Override
    public Future<OneClickConfig> getOneClickConfig(final String configId) {
        validationService.validate(NotBlankValidator.of("configId", configId));

        return doGet("api/one-click/configs/" + Util.safeEncode(configId), ImmutableList.of(Status.OK, Status.NOT_FOUND))
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, OneClickConfig.class));
    }

    @Override
    public Future<String> reverseStrexTransaction(final String transactionId) {
        validationService.validate(NotBlankValidator.of("transactionId", transactionId));

        return doDelete("api/strex/transactions/" + Util.safeEncode(transactionId), Status.CREATED)
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response));
    }

    @Override
    public Future<UserValidity> getStrexUserValidity(final String recipient, final String merchantId) {
        validationService.validate(NotBlankValidator.of("recipient", recipient));

        return doGet("api/strex/validity?recipient=" + Util.safeEncode(recipient) + ((merchantId != null && !merchantId.isEmpty()) ? "&merchantId=" + Util.safeEncode(merchantId) : ""),
            ImmutableList.of(Status.OK, Status.NOT_FOUND))
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, UserValidity.class));
    }

    @Override
    public Future<Boolean> verifySignature(
            final String method, final String uri, final String content, final String xEcdsaSignatureString
    ) {
        validationService.validate(NotBlankValidator.of("method", method), NotBlankValidator.of("uri", uri),
                NotNullValidator.of("content", content), NotBlankValidator.of("xEcdsaSignatureString", xEcdsaSignatureString),
                PatternValidator.of("xEcdsaSignatureString", xEcdsaSignatureString, X_ECDSA_SIGNATURE_PATTERN));

        final String[] parts = xEcdsaSignatureString.split(":");
        final String keyName = parts[0];
        final long timestamp = Long.parseLong(parts[1]);
        final String nonce = parts[2];
        final String sign = parts[3];

        validationService.validate(NotBlankValidator.of("keyName", keyName), TimestampValidator.of("timestamp", timestamp, 5 * 60),
                NotBlankValidator.of("nonce", nonce), NotBlankValidator.of("sign", sign));

        return doGet("api/public-key/" + Util.safeEncode(keyName), Status.OK)
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(json -> Util.wrap(() -> new ObjectMapper().readTree(json).get("publicKeyString").asText()))
                .thenApplyAsync(ecPrivateKeyAsString -> authorizationService.verifyHeader(EcdsaVerifier.getInstance(ecPrivateKeyAsString),
                        method, uri, timestamp, nonce, content, sign));
    }

    @Override
    public Future<PublicKey> getServerPublicKey(final String keyName) {
        validationService.validate(NotBlankValidator.of("keyName", keyName));

        return doGet("api/server/public-keys/" + Util.safeEncode(keyName), ImmutableList.of(Status.OK, Status.NOT_FOUND))
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, PublicKey.class));
    }

    @Override
    public Future<List<PublicKey>> getClientPublicKeys() {
        return doGet("api/client/public-keys", Status.OK)
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, Type.LIST_OF_PUBLIC_KEYS));
    }

    @Override
    public Future<PublicKey> getClientPublicKey(final String keyName) {
        validationService.validate(NotBlankValidator.of("keyName", keyName));

        return doGet("api/client/public-keys/" + Util.safeEncode(keyName), ImmutableList.of(Status.OK, Status.NOT_FOUND))
                .thenApplyAsync(response -> responseParsers.get(response.code()).parse(response))
                .thenApplyAsync(string -> objectMappingService.toObject(string, PublicKey.class));
    }

    @Override
    public Future<Void> deleteClientPublicKey(final String keyName) {
        validationService.validate(NotBlankValidator.of("keyName", keyName));

        return doDelete("api/client/public-keys/" + Util.safeEncode(keyName), Status.NO_CONTENT)
                .thenApplyAsync(response -> VOID);
    }

    /**
     * Performs standard GET call to the server
     *
     * @param path Path to be called (should <b>not</b> include base URL)
     * @param code Expected response status code
     * @return Future which contains response
     */
    private CompletableFuture<Response> doGet(final String path, final Integer code) {
        return doGet(path, ImmutableList.of(), ImmutableList.of(code));
    }

    /**
     * Performs standard GET call to the server
     *
     * @param path  Path to be called (should <b>not</b> include base URL)
     * @param codes Expected response status codes
     * @return Future which contains response
     */
    private CompletableFuture<Response> doGet(final String path, final List<Integer> codes) {
        return doGet(path, ImmutableList.of(), codes);
    }

    /**
     * Performs standard GET call to the server
     *
     * @param path   Path to be called (should <b>not</b> include base URL)
     * @param params Query params to be sent
     * @param code   Expected response status code
     * @return Future which contains response
     */
    private CompletableFuture<Response> doGet(final String path, final List<Param> params, final Integer code) {
        return doGet(path, params, ImmutableList.of(code));
    }

    /**
     * Performs standard GET call to the server
     * Query parameters could be passed as a list of {@link Param}'s
     *
     * @param path   Path to be called (should <b>not</b> include base URL)
     * @param params Query params to be sent
     * @param codes  Expected response status codes
     * @return {@link CompletableFuture} which contains response
     */
    private CompletableFuture<Response> doGet(final String path, final List<Param> params, final List<Integer> codes) {
        final String uri = parameters.getBaseUrl() + path + params.stream()
                .filter(p -> !Objects.isNull(p.getValue()))
                .map(Param::toQueryParam).reduce((s1, s2) -> s1 + "&" + s2)
                .map(p -> "?" + p).orElse("");

        final String authorization = authorizationService.signHeader(signer, parameters.getKeyName(), Method.GET, uri, "");

        final Request request = new Request.Builder().url(uri).get()
                .header(Header.AUTHORIZATION, authorization).build();

        final Call call = okHttpClient.newCall(request);

        return CompletableFuture.supplyAsync(() -> Util.wrap(call::execute))
                .thenApplyAsync(response -> Util.wrap(() -> responseHandler.handle(response, codes)));
    }

    /**
     * Performs standard POST call to the server
     *
     * @param path    Path to be called (should <b>not</b> include base URL)
     * @param content Body to be sent with the request as a json string
     * @param code    Expected response status code
     * @return {@link CompletableFuture} which contains response
     */
    private CompletableFuture<Response> doPost(final String path, final String content, final Integer code) {
        return doPost(path, content, ImmutableList.of(code));
    }

    /**
     * Performs standard POST call to the server
     *
     * @param path    Path to be called (should <b>not</b> include base URL)
     * @param content Body to be sent with the request as a json string
     * @param codes   Expected response status codes
     * @return {@link CompletableFuture} which contains response
     */
    private CompletableFuture<Response> doPost(final String path, final String content, final List<Integer> codes) {
        final String uri = parameters.getBaseUrl() + path;
        final String authorization = authorizationService.signHeader(signer, parameters.getKeyName(), Method.POST, uri, content);

        final Request request = new Request.Builder()
                .url(uri).post(RequestBody.create(MediaType.APPLICATION_JSON, content))
                .header(Header.AUTHORIZATION, authorization).build();

        final Call call = okHttpClient.newCall(request);

        return CompletableFuture.supplyAsync(() -> Util.wrap(call::execute))
                .thenApplyAsync(response -> Util.wrap(() -> responseHandler.handle(response, codes)));
    }

    /**
     * Performs standard PUT call to the server
     *
     * @param path    Path to be called (should <b>not</b> include base URL)
     * @param content Body to be sent with the request as a json string
     * @param code    Expected response status code
     * @return {@link CompletableFuture} which contains response
     */
    private CompletableFuture<Response> doPut(final String path, final String content, final Integer code) {
        return doPut(path, content, ImmutableList.of(code));
    }

    /**
     * Performs standard PUT call to the server
     *
     * @param path    Path to be called (should <b>not</b> include base URL)
     * @param content Body to be sent with the request as a json string
     * @param codes   Expected response status codes
     * @return {@link CompletableFuture} which contains response
     */
    private CompletableFuture<Response> doPut(final String path, final String content, final List<Integer> codes) {
        final String uri = parameters.getBaseUrl() + path;
        final String authorization = authorizationService.signHeader(signer, parameters.getKeyName(), Method.PUT, uri, content);

        final Request request = new Request.Builder()
                .url(uri).put(RequestBody.create(MediaType.APPLICATION_JSON, content))
                .header(Header.AUTHORIZATION, authorization).build();

        final Call call = okHttpClient.newCall(request);

        return CompletableFuture.supplyAsync(() -> Util.wrap(call::execute))
                .thenApplyAsync(response -> Util.wrap(() -> responseHandler.handle(response, codes)));
    }

    /**
     * Performs standard DELETE call to the server
     *
     * @param path Path to be called (should <b>not</b> include base URL)
     * @param code Expected response status code
     * @return {@link CompletableFuture} which contains response
     */
    private CompletableFuture<Response> doDelete(final String path, final Integer code) {
        return doDelete(path, ImmutableList.of(code));
    }

    /**
     * Performs standard DELETE call to the server
     *
     * @param path  Path to be called (should <b>not</b> include base URL)
     * @param codes Expected response status codes
     * @return {@link CompletableFuture} which contains response
     */
    private CompletableFuture<Response> doDelete(final String path, final List<Integer> codes) {
        final String uri = parameters.getBaseUrl() + path;
        final String authorization = authorizationService.signHeader(signer, parameters.getKeyName(), Method.DELETE, uri, "");

        final Request request = new Request.Builder().url(uri).delete()
                .header(Header.AUTHORIZATION, authorization).build();

        final Call call = okHttpClient.newCall(request);

        return CompletableFuture.supplyAsync(() -> Util.wrap(call::execute))
                .thenApplyAsync(response -> Util.wrap(() -> responseHandler.handle(response, codes)));
    }

    /**
     * Gets a new client
     *
     * @param ecPrivateKeyAsString EC private key as a string
     * @param parameters           Parameters
     * @return A client
     */
    public static Target365Client getInstance(final String ecPrivateKeyAsString, final Parameters parameters) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(parameters.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(parameters.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(parameters.getWriteTimeout(), TimeUnit.MILLISECONDS).build();

        final Map<Integer, ResponseParser> responseParsers = ImmutableMap.of(
                Status.OK, new OkResponseParser(),
                Status.NOT_FOUND, new NotFoundResponseParser(),
                Status.CREATED, new CreatedResponseParser()
        );

        return new Target365Client(parameters, okHttpClient, new InvalidResponseHandler(),
                EcdsaSigner.getInstance(ecPrivateKeyAsString), new EcdsaAuthorizationService(),
                JacksonObjectMappingService.getInstance(), new Jsr303ValidationService(),
                responseParsers);
    }

    @Getter
    @AllArgsConstructor
    public static final class Parameters {

        /**
         * Default connection timeout in milliseconds
         */
        public static final int DEFAULT_TIMEOUT = 60_000;

        private final String baseUrl;
        private final String keyName;
        private final int connectTimeout;
        private final int readTimeout;
        private final int writeTimeout;

        public Parameters(final String baseUrl, final String keyName) {
            this.baseUrl = baseUrl;
            this.keyName = keyName;
            this.connectTimeout = DEFAULT_TIMEOUT;
            this.readTimeout = DEFAULT_TIMEOUT;
            this.writeTimeout = DEFAULT_TIMEOUT;
        }
    }

    @Getter
    @AllArgsConstructor
    public static final class Param {

        private final String name;
        private final String value;

        /**
         * Returns query param representation of the current param
         * Also, encodes value of the param using {@code {@link URLEncoder }}
         *
         * @return query param representation of the current param
         */
        public String toQueryParam() {
            return name + "=" + Util.safeEncode(value);
        }
    }

    /**
     * Header
     */
    @UtilityClass
    public static final class Header {

        private static final String AUTHORIZATION = "Authorization";
        public static final String LOCATION = "Location";
    }

    /**
     * Method
     */
    @UtilityClass
    public static final class Method {

        private static final String GET = "GET";
        private static final String POST = "POST";
        private static final String PUT = "PUT";
        private static final String DELETE = "DELETE";
    }

    /**
     * Status
     */
    @UtilityClass
    private static final class Status {

        private static final int OK = 200;
        private static final int CREATED = 201;
        private static final int NO_CONTENT = 204;
        private static final int NOT_FOUND = 404;
    }

    /**
     * Media types
     */
    @UtilityClass
    private static final class MediaType {

        private static final okhttp3.MediaType APPLICATION_JSON = okhttp3.MediaType.parse("application/json");
    }

    /**
     * Type
     */
    @UtilityClass
    private static final class Type {

        private static final TypeReference<List<Keyword>> LIST_OF_KEYWORDS = new TypeReference<List<Keyword>>() {
        };

        private static final TypeReference<List<StrexMerchantId>> LIST_OF_MERCHANTS = new TypeReference<List<StrexMerchantId>>() {
        };

        private static final TypeReference<List<PublicKey>> LIST_OF_PUBLIC_KEYS = new TypeReference<List<PublicKey>>() {
        };
    }
}
