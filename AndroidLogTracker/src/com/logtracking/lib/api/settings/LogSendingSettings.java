package com.logtracking.lib.api.settings;

/**
 * General interface for sending or uploading service settings.
 */
public interface LogSendingSettings {

    /**
     * Enum representing possible services for sending bug reports.
     */
	public enum SendingService {

        /**
         * Represent sending bug report via email.
         * @see  EmailLogSendingSettings
         */
		E_MAIL,
	}

    /**
     * @return type of service that used for sending or uploading.
     */
	public SendingService getServiceType();
}
