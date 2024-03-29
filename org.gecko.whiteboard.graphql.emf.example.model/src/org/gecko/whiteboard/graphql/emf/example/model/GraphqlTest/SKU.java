/**
 */
package org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>SKU</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.SKU#getId <em>Id</em>}</li>
 * </ul>
 *
 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getSKU()
 * @model
 * @generated
 */
public interface SKU extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getSKU_Id()
	 * @model id="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.SKU#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

} // SKU
