/**
 */
package org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.DataFetcherTest;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestPackage;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.SKU;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Fetcher Test</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.DataFetcherTestImpl#getTest <em>Test</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.DataFetcherTestImpl#getSkus <em>Skus</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DataFetcherTestImpl extends MinimalEObjectImpl.Container implements DataFetcherTest {
	/**
	 * The default value of the '{@link #getTest() <em>Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTest()
	 * @generated
	 * @ordered
	 */
	protected static final String TEST_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTest() <em>Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTest()
	 * @generated
	 * @ordered
	 */
	protected String test = TEST_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSkus() <em>Skus</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSkus()
	 * @generated
	 * @ordered
	 */
	protected EList<SKU> skus;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DataFetcherTestImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GraphQLTestPackage.Literals.DATA_FETCHER_TEST;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTest() {
		return test;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTest(String newTest) {
		String oldTest = test;
		test = newTest;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GraphQLTestPackage.DATA_FETCHER_TEST__TEST, oldTest, test));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SKU> getSkus() {
		if (skus == null) {
			skus = new EObjectResolvingEList<SKU>(SKU.class, this, GraphQLTestPackage.DATA_FETCHER_TEST__SKUS);
		}
		return skus;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GraphQLTestPackage.DATA_FETCHER_TEST__TEST:
				return getTest();
			case GraphQLTestPackage.DATA_FETCHER_TEST__SKUS:
				return getSkus();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case GraphQLTestPackage.DATA_FETCHER_TEST__TEST:
				setTest((String)newValue);
				return;
			case GraphQLTestPackage.DATA_FETCHER_TEST__SKUS:
				getSkus().clear();
				getSkus().addAll((Collection<? extends SKU>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case GraphQLTestPackage.DATA_FETCHER_TEST__TEST:
				setTest(TEST_EDEFAULT);
				return;
			case GraphQLTestPackage.DATA_FETCHER_TEST__SKUS:
				getSkus().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case GraphQLTestPackage.DATA_FETCHER_TEST__TEST:
				return TEST_EDEFAULT == null ? test != null : !TEST_EDEFAULT.equals(test);
			case GraphQLTestPackage.DATA_FETCHER_TEST__SKUS:
				return skus != null && !skus.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (test: ");
		result.append(test);
		result.append(')');
		return result.toString();
	}

} //DataFetcherTestImpl
