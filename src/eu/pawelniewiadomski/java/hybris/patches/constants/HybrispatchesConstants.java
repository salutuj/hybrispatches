
package eu.pawelniewiadomski.java.hybris.patches.constants;


public final class HybrisPatchesConstants extends GeneratedHybrispatchesConstants
{
	public static final String EXTENSIONNAME = "hybrispatches";
	public static final String PATCHES_PATH = "/" + EXTENSIONNAME + "/import/patches";

	public static final String CFG_SOLRCONFIG = "hybrispatches.solr.configuration";
	public static final String CFG_CATALOGNAME = "hybrispatches.catalog.name";
	public static final String CFG_INDEXSOLR = "hybrispatches.init.indexing.solr";
	public static final String CFG_SYNCCATALOGS = "hybrispatches.init.synchronize.catalogs";

	public static final String CFG_MAIL_ADDRESSFROM = "hybrispatches.mail.address.from";
	public static final String CFG_MAIL_ADDRESSTO = "hybrispatches.mail.address.to";
	public static final String CFG_MAIL_USERNAME = "hybrispatches.mail.username";
	public static final String CFG_MAIL_SUBJECT = "hybrispatches.mail.subject";

	private HybrisPatchesConstants()
	{
		//empty to avoid instantiating this constant class
	}

}
