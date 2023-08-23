package org.gecko.whiteboard.graphql.dto;

import org.osgi.dto.DTO;
import org.osgi.framework.dto.ServiceReferenceDTO;

/**
 * Represents the state of a GraphQL Service Runtime.
 * 
 * @NotThreadSafe
 * @author $Id: ca2f52924a0647cd7beb472bce41360aec1ddc4d $
 */
public class RuntimeDTO extends DTO {

	/**
	 * The DTO for the corresponding {@code GraphQLServiceRuntime}. This value is
	 * never {@code null}.
	 */
	public ServiceReferenceDTO serviceDTO;

	public String[] queries;

	public String[] mutations;

	public GraphQLQueryProviderDTO[] queryProviderDTOs;

	public GraphQLTypesProviderDTO[] typesProviderDTOs;

	// TODO: create DTOs and define such typed fields for 'GraphQLMutationProvider', 'GraphQLSubscriptionProvider', etc.  - as needed
}