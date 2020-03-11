/**
 */
package org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Catalog Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry#getId <em>Id</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry#getName <em>Name</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry#getDescription <em>Description</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry#getCatalog <em>Catalog</em>}</li>
 * </ul>
 *
 * @see org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestPackage#getCatalogEntry()
 * @model
 * @generated
 */
public interface CatalogEntry extends EObject {
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
	 * @see org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestPackage#getCatalogEntry_Id()
	 * @model id="true" required="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The name of the Catalog Entry
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestPackage#getCatalogEntry_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestPackage#getCatalogEntry_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Catalog</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog#getEntries <em>Entries</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Catalog</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Catalog</em>' reference.
	 * @see #setCatalog(Catalog)
	 * @see org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestPackage#getCatalogEntry_Catalog()
	 * @see org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog#getEntries
	 * @model opposite="entries"
	 * @generated
	 */
	Catalog getCatalog();

	/**
	 * Sets the value of the '{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry#getCatalog <em>Catalog</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Catalog</em>' reference.
	 * @see #getCatalog()
	 * @generated
	 */
	void setCatalog(Catalog value);

} // CatalogEntry
