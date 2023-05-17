/**
 */
package org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Fetcher Test</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.DataFetcherTest#getTest <em>Test</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.DataFetcherTest#getSkus <em>Skus</em>}</li>
 * </ul>
 *
 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getDataFetcherTest()
 * @model
 * @generated
 */
public interface DataFetcherTest extends EObject {
	/**
	 * Returns the value of the '<em><b>Test</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Test</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Test</em>' attribute.
	 * @see #setTest(String)
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getDataFetcherTest_Test()
	 * @model
	 * @generated
	 */
	String getTest();

	/**
	 * Sets the value of the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.DataFetcherTest#getTest <em>Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Test</em>' attribute.
	 * @see #getTest()
	 * @generated
	 */
	void setTest(String value);

	/**
	 * Returns the value of the '<em><b>Skus</b></em>' reference list.
	 * The list contents are of type {@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.SKU}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Skus</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Skus</em>' reference list.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getDataFetcherTest_Skus()
	 * @model annotation="GraphQLContext dataFetcherTarget='(test=1)'"
	 * @generated
	 */
	EList<SKU> getSkus();

} // DataFetcherTest
