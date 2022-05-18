package br.com.zenix.core.spigot.twitter;

public enum TwitterAccount {

	ZENIX_BANS("vHpMIWwS3w7xWL4NJHzgKUx1X", "qj03mjlYOrrKptFU703JmFhIsUMAXxAoWEsWXWpMoo8CIpm4Oy", "891147209659609088-XoFUhTm5pRTmCWPTctyFXsxHLVBxSnC", "RAgSIv09w7O0hlDRLDkijJ1dKIMWTJG6Lwrn9eIIu9UoA"),
	ZENIXCC("GwgkPtqLBypsFOQtFgr6eGPCp", "oK93ARQJ3bxYIVy3Q5OS7qXKWipgXBVdQxqwnT40PFb5oxUnPw", "891145961262772224-jXh4cXpMMCMAYJQ9iA3Us5YnDS5vREl", "E8s0JVoiws2hD9EQkH0JFkYdaVQgsRPgS8JEJXHUzqs35");

	private final String consumerKey, consumerSecret, accessToken, accessSecret;

	TwitterAccount(String consumerKey, String consumerSecret, String accessToken, String accessSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessSecret = accessSecret;
		this.accessToken = accessToken;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

}
