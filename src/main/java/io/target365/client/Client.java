package io.target365.client;

public interface Client extends PingClient, KeywordClient, LookupClient, StrexClient,
        OutMessageClient, InMessageClient, VerificationClient, PublicKeysClient, PincodeClient { }