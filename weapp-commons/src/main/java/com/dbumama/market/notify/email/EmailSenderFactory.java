package com.dbumama.market.notify.email;

public class EmailSenderFactory {

	static IEmailSender sender;

	public static IEmailSender createSender() {

		if (sender == null) {
			sender = new SimplerEmailSender();
		}

		return sender;

	}

}
