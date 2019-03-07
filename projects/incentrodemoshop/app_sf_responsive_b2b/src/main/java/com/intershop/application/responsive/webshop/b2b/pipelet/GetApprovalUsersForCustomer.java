package com.intershop.application.responsive.webshop.b2b.pipelet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import com.intershop.beehive.core.capi.log.Logger;
import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.component.b2b.role.capi.customer.CustomerBOSecurityExtension;
import com.intershop.beehive.core.capi.security.Permission;
import com.intershop.beehive.core.capi.security.PermissionMgr;
import com.intershop.component.customer.capi.CustomerBO;
import com.intershop.component.user.capi.UserBO;

/**
 *	Gets the users of a	customer having	Order Approval permission.
 */
public class GetApprovalUsersForCustomer extends Pipelet
{
	public static final String DN_CUSTOMER_BO = "CustomerBO";
	public static final String DN_APPROVAL_USERS = "ApprovalUsers";

   /**
	* The ID of	the	B2B	ORDER APPROVAL permission
	*/
	public static final String ORDER_APPROVAL_PERMISSION = "APP_B2B_ORDER_APPROVAL";

	@Inject
	private PermissionMgr permissionMgr;

	public int execute(PipelineDictionary dict)	throws PipeletExecutionException
	{
		// lookup 'CustomerBO' in pipeline dictionary
		CustomerBO customer = dict.getRequired(DN_CUSTOMER_BO);

		if (!customer.getCustomerType().isApplicableExtension("GroupCustomer"))
		{
			// no B2B user
			dict.put(DN_APPROVAL_USERS, Collections.emptyList());
			return PIPELET_NEXT;
		}

		CustomerBOSecurityExtension extension = customer.getExtension(CustomerBOSecurityExtension.class);
		if (extension == null)
		{
			// order approval extension not available
			dict.put(DN_APPROVAL_USERS, Collections.emptyList());
			return PIPELET_NEXT;
		}

		Permission	permission;
		permission = permissionMgr.getPermission(ORDER_APPROVAL_PERMISSION); //	Retrieve permission	from DB
		if (permission	==	null)
		{
			Logger.error(this, "Permission ID '{}' not found in DB", ORDER_APPROVAL_PERMISSION);
			dict.put(DN_APPROVAL_USERS, Collections.emptyList());
			return PIPELET_NEXT;
		}

		Collection<UserBO> approvalUsers = new ArrayList<UserBO>(extension.getUsersByPermission(permission));
		dict.put(DN_APPROVAL_USERS, approvalUsers);
		return PIPELET_NEXT;
	}
}