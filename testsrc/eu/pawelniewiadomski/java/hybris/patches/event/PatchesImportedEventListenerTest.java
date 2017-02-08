package eu.pawelniewiadomski.java.hybris.patches.event;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import eu.pawelniewiadomski.java.hybris.patches.email.EmailContext;
import eu.pawelniewiadomski.java.hybris.patches.email.EmailRenderer;


@UnitTest
public class PatchesImportedEventListenerTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PatchesImportedEventListenerTest.class.getName());


	@Mock
	private EmailService emailService;

	@Mock
	private EmailRenderer emailRenderer;

	@Mock
	private EmailContext emailContext;

	@InjectMocks
	private final PatchesImportedEventListener patchesImportedEventListener = new PatchesImportedEventListener();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown()
	{
		// NOP
	}

	@Test
	public void testNullEvent()
	{
		try
		{
			patchesImportedEventListener.onEvent(null);
			Assert.fail();
		}
		catch (final NullPointerException ex)
		{
			// this is correct
		}
	}


	@Test
	public void testEventWithNullPatchesImportDataSuccess()
	{
		final PatchesImportedEvent event = new PatchesImportedEvent();
		final EmailMessageModel emailMessageModel = prepareEmailModelInCommonGivenSection(event);
		when(Boolean.valueOf(emailService.send(emailMessageModel))).thenReturn(Boolean.TRUE);
		when(emailContext.getSubject()).thenReturn("Subject");
		patchesImportedEventListener.onEvent(event);
	}

	@Test
	public void testEventWithNullPatchesImportDataFailure()
	{
		final PatchesImportedEvent event = new PatchesImportedEvent();
		final EmailMessageModel emailMessageModel = prepareEmailModelInCommonGivenSection(event);
		doReturn(Boolean.TRUE).when(emailService).send(emailMessageModel);
		patchesImportedEventListener.onEvent(event);
	}

	private EmailMessageModel prepareEmailModelInCommonGivenSection(final PatchesImportedEvent event)
	{
		final String emailSubject = "Subject";
		final String emailBody = "";
		final String emailAddressTo = "test-to@test.pl";
		final String emailAddressToName = "Test To";
		final String emailAddressFrom = "test-from@test.pl";
		final String emailAddressFromName = "Test From";

		event.setPatchesImportData(null);
		when(emailContext.getSubject()).thenReturn(emailSubject);
		when(emailContext.getToEmail()).thenReturn(emailAddressTo);
		when(emailContext.getToDisplayName()).thenReturn(emailAddressToName);
		when(emailContext.getFromEmail()).thenReturn(emailAddressFrom);
		when(emailContext.getFromDisplayName()).thenReturn(emailAddressFromName);
		when(emailRenderer.renderSubjectWithPatchesImportData(emailSubject, null)).thenReturn(emailSubject);
		when(emailRenderer.renderBodyWithPatchesImportData(null)).thenReturn(emailBody);
		final EmailAddressModel emailAddressToModel = new EmailAddressModel();
		emailAddressToModel.setEmailAddress(emailAddressTo);
		emailAddressToModel.setDisplayName(emailAddressToName);
		when(emailService.getOrCreateEmailAddressForEmail(emailAddressTo, emailAddressToName)).thenReturn(emailAddressToModel);
		final EmailAddressModel emailAddressFromModel = new EmailAddressModel();
		emailAddressFromModel.setEmailAddress(emailAddressFrom);
		emailAddressFromModel.setDisplayName(emailAddressFromName);
		when(emailService.getOrCreateEmailAddressForEmail(emailAddressFrom, emailAddressFromName))
				.thenReturn(emailAddressFromModel);
		final EmailMessageModel emailMessageModel = new EmailMessageModel();
		/*
		 * when(emailService.createEmailMessage(Mockito.anyList(), Mockito.anyList(), Mockito.anyList(),
		 * emailAddressFromModel, eq(emailAddressFrom), eq(emailSubject), eq(emailBody),
		 * Mockito.anyList())).thenReturn(emailMessageModel);
		 */
		return emailMessageModel;
	}


}
