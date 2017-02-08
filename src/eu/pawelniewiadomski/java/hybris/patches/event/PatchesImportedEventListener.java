
package eu.pawelniewiadomski.java.hybris.patches.event;

import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import eu.pawelniewiadomski.java.hybris.patches.data.PatchImportData;
import eu.pawelniewiadomski.java.hybris.patches.email.EmailContext;
import eu.pawelniewiadomski.java.hybris.patches.email.EmailRenderer;



/*
 * This class handles mail sending
 */
public class PatchesImportedEventListener extends AbstractEventListener<PatchesImportedEvent>
{
	private static Logger LOG = Logger.getLogger(PatchesImportedEventListener.class);


	private EmailService emailService;
	private EmailRenderer emailRenderer;
	private EmailContext emailContext;


	@Override
	protected void onEvent(final PatchesImportedEvent event)
	{
		LOG.debug("PatchesImportedEvent received:" + event);
		final Map<String, PatchImportData> patchesImportData = event.getPatchesImportData();
		final String renderedSubject = getEmailRenderer().renderSubjectWithPatchesImportData(emailContext.getSubject(),
				patchesImportData);
		final String renderedBody = getEmailRenderer().renderBodyWithPatchesImportData(patchesImportData);
		createAndSendMail(renderedSubject, renderedBody);
	}

	private void createAndSendMail(final String renderedSubject, final String renderedBody)
	{
		final EmailMessageModel emailMessageModel = createEmailMessage(renderedSubject, renderedBody);
		if (emailMessageModel != null)
		{
			if (getEmailService().send(emailMessageModel))
			{
				LOG.info("Email with title '" + renderedSubject + "' sent");
			}
			else
			{
				LOG.warn("Unable to send email: " + renderedSubject);
			}
		}
	}

	protected EmailMessageModel createEmailMessage(final String emailSubject, final String emailBody)
	{
		final List<EmailAddressModel> toEmails = new ArrayList<EmailAddressModel>();
		final EmailAddressModel toAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getToEmail(),
				emailContext.getToDisplayName());
		toEmails.add(toAddress);
		final EmailAddressModel fromAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getFromEmail(),
				emailContext.getFromDisplayName());
		return getEmailService().createEmailMessage(toEmails, new ArrayList<EmailAddressModel>(),
				new ArrayList<EmailAddressModel>(), fromAddress, emailContext.getFromEmail(), emailSubject, emailBody, null);
	}

	public EmailContext getEmailContext()
	{
		return emailContext;
	}

	@Required
	public void setEmailContext(final EmailContext emailContext)
	{
		this.emailContext = emailContext;
	}

	protected EmailService getEmailService()
	{
		return emailService;
	}

	@Required
	public void setEmailService(final EmailService emailService)
	{
		this.emailService = emailService;
	}


	public EmailRenderer getEmailRenderer()
	{
		return emailRenderer;
	}

	@Required
	public void setEmailRenderer(final EmailRenderer emailRenderer)
	{
		this.emailRenderer = emailRenderer;
	}



}
