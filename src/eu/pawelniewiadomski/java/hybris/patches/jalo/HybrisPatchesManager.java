package eu.pawelniewiadomski.java.hybris.patches.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import eu.pawelniewiadomski.java.hybris.patches.constants.HybrisPatchesConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class HybrisPatchesManager extends GeneratedHybrisPatchesManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( HybrisPatchesManager.class.getName() );
	
	public static final HybrisPatchesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (HybrisPatchesManager) em.getExtension(HybrisPatchesConstants.EXTENSIONNAME);
	}
	
}
