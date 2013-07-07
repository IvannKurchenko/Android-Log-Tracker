package com.logtracking.lib.api.config;

/**
 * General interface for sending or uploading service config.
 */
public interface LogSendingConfiguration {

    /**
     * Enum representing possible services for sending bug reports.
     */
	public enum SendingService {

        /**
         * Represent sending bug report via email.
         * @see  EmailLogSendingConfiguration
         */
		E_MAIL,
	}

    /**
     * @return type of service that used for sending or uploading.
     */
	public SendingService getServiceType();
}
