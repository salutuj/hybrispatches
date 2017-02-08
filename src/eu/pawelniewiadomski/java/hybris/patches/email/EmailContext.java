package eu.pawelniewiadomski.java.hybris.patches.email;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.springframework.beans.factory.annotation.Required;

import eu.pawelniewiadomski.java.hybris.patches.constants.HybrisPatchesConstants;



public class EmailContext
{
	private ConfigurationService configurationService;


	public String getSubject()
	{
		return configurationService.getConfiguration().getString(HybrisPatchesConstants.CFG_MAIL_SUBJECT);
	}

	public String getToEmail()
	{
		return configurationService.getConfiguration().getString(HybrisPatchesConstants.CFG_MAIL_ADDRESSTO);
	}


	public String getFromEmail()
	{
		return configurationService.getConfiguration().getString(HybrisPatchesConstants.CFG_MAIL_ADDRESSFROM);
	}


	public String getToDisplayName()
	{
		return configurationService.getConfiguration().getString(HybrisPatchesConstants.CFG_MAIL_USERNAME);
	}


	public String getFromDisplayName()
	{
		return configurationService.getConfiguration().getString(HybrisPatchesConstants.CFG_MAIL_USERNAME);
	}


	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}


	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}




}
