package org.gecko.whiteboard.graphql.dto;

import org.osgi.dto.DTO;
import org.osgi.framework.dto.ServiceReferenceDTO;

/**
 * Represents the state of a GraphQL Service Runtime.
 * 
 * @NotThreadSafe
 * @author $Id: 1ce50780721d79f0a239ca29d2c575a2ccd4b442 $
 */
public class GraphQLRuntimeDTO extends DTO {

	/**
	 * The DTO for the corresponding {@code GraphQLServiceRuntime}. This value is
	 * never {@code null}.
	 */
	public ServiceReferenceDTO		serviceDTO;

}