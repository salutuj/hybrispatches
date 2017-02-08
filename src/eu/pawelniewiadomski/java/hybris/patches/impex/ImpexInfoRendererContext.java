package eu.pawelniewiadomski.java.hybris.patches.impex;


class ImpexInfoRendererContext
{
	enum ImpexesStatus
	{
		IMPORTED(" - imported"), SKIPPED(" - skipped"), INCORRECT(" - incorrect");

		private String suffix;

		ImpexesStatus(final String suffix)
		{
			this.suffix = suffix;
		}

		public String getStatusSuffix()
		{
			return suffix;
		}
	}

	enum RenderingScope
	{
		HEADERS_ONLY, FULL_DETAILS
	}

	ImpexesStatus impexesStatus;
	RenderingScope renderingScope;
	final private StringBuilder messageBuilder;


	public ImpexInfoRendererContext(final RenderingScope renderingScope)
	{
		messageBuilder = new StringBuilder(2048);
		this.renderingScope = renderingScope;
		impexesStatus = null;
	}


	public StringBuilder getMessageBuilder()
	{
		return messageBuilder;
	}
}
