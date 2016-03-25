package ru.rostvertolplc.osapr.tc04.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil; //import java.net.*;

//import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.*; //import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import ru.rostvertolplc.osapr.helpers.*;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class MainHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public MainHandler() {
	}

	public boolean checkTypeList(TCComponent item, String[] typeList) {
		for (String str : typeList) {
			if (item.getType().equals(str)) {
				return true;
			}
		}
		return false;
	}

	public String GetNewObjName(TCComponentItem item, String[] typeList)
			throws TCException {
		TCComponent relatedComponent = item
				.getRelatedComponent("IMAN_master_form");
		return relatedComponent.getProperty("HR_NAME") + " "
				+ relatedComponent.getProperty("HR48") + "-"
				+ relatedComponent.getProperty("HR_OBOZN");
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		// AbstractAIFUIApplication currentApplication =
		// AIFUtility.getCurrentApplication();
		// TCSession session = (TCSession)currentApplication.getSession();
		// AIFComponentContext context = currentApplication.getTargetContext();

		InterfaceAIFComponent[] c_targets = AIFUtility.getTargetComponents();
		InterfaceAIFComponent c_target = null;

		if (c_targets != null) {

			String newName = null;
			StringBuilder s1 = new StringBuilder();
			for (InterfaceAIFComponent interfaceAIFComponent : c_targets) {
				try {
					s1.append(((TCComponent) interfaceAIFComponent).getProperty("object_name")).append("\n");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			if (MessageDialog.openConfirm(window.getShell(), "Teamcenter",
					"Переименовать объекты в соответствие с атрибутами мастер-формы? \n"
							+ s1.toString())) {
				String[] typeList = PreferenceHelper
						.getPreferenceValueArray("RVT_TC04_TYPES");
				if ((typeList == null) || (typeList.length == 0)) {
					typeList = new String[] { "H47_Standart_Izd",
							"H47_Standart_Izd Revision" };
				}
				for (int i = 0; i < c_targets.length; i++) {
					c_target = c_targets[i];
					if (checkTypeList((TCComponent) c_target, typeList)) {
						if (c_target instanceof TCComponentItemRevision) {
							TCComponentItemRevision itemrev1 = (TCComponentItemRevision) c_target;
							try {
								newName = GetNewObjName(itemrev1.getItem(),
										typeList);
							} catch (Exception e) {
								// TODO: handle exception
							}
						} else if (c_target instanceof TCComponentItem) {
							try {
								newName = GetNewObjName(
										(TCComponentItem) c_target, typeList);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						if (newName != null) {
							try {
								((TCComponent) c_target).lock();
								((TCComponent) c_target).setProperty(
										"object_name", newName);
								((TCComponent) c_target).save();
								((TCComponent) c_target).unlock();
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
					// InetAddress address =
					// InetAddress.getByName("ws-005-osapr");
					/*
					 * try { MessageDialog.openInformation( window.getShell(),
					 * "Инфа: " + address.getHostAddress(),
					 * itemrev1.getProperty("object_name")); } catch (Exception
					 * e) { // TODO: handle exception }
					 */
				}

			}
		}

		/*
		 * if (context != null) {
		 * 
		 * }
		 * 
		 * MessageDialog.openInformation( window.getShell(), "Инфа:" +
		 * context.getClientData().toString(), "000");
		 */

		return null;
	}
}