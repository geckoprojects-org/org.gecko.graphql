/**
 */
package org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl;

import java.math.BigInteger;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Currency;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestPackage;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Product;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.SKU;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Product</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.ProductImpl#getSkus <em>Skus</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.ProductImpl#getSize <em>Size</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.ProductImpl#isActive <em>Active</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.ProductImpl#getPrice <em>Price</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.ProductImpl#getCurrency <em>Currency</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.ProductImpl#getInputTest <em>Input Test</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.ProductImpl#getOutputTest <em>Output Test</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.ProductImpl#getSkusMutationOnly <em>Skus Mutation Only</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.ProductImpl#getInputValueList <em>Input Value List</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.ProductImpl#getCurrencies <em>Currencies</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ProductImpl extends CatalogEntryImpl implements Product {
	/**
	 * The cached value of the '{@link #getSkus() <em>Skus</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSkus()
	 * @generated
	 * @ordered
	 */
	protected EList<SKU> skus;

	/**
	 * The default value of the '{@link #getSize() <em>Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSize()
	 * @generated
	 * @ordered
	 */
	protected static final BigInteger SIZE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSize() <em>Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSize()
	 * @generated
	 * @ordered
	 */
	protected BigInteger size = SIZE_EDEFAULT;

	/**
	 * The default value of the '{@link #isActive() <em>Active</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isActive()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ACTIVE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isActive() <em>Active</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isActive()
	 * @generated
	 * @ordered
	 */
	protected boolean active = ACTIVE_EDEFAULT;

	/**
	 * The default value of the '{@link #getPrice() <em>Price</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrice()
	 * @generated
	 * @ordered
	 */
	protected static final double PRICE_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getPrice() <em>Price</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrice()
	 * @generated
	 * @ordered
	 */
	protected double price = PRICE_EDEFAULT;

	/**
	 * The default value of the '{@link #getCurrency() <em>Currency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrency()
	 * @generated
	 * @ordered
	 */
	protected static final Currency CURRENCY_EDEFAULT = Currency.DOLLAR;

	/**
	 * The cached value of the '{@link #getCurrency() <em>Currency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrency()
	 * @generated
	 * @ordered
	 */
	protected Currency currency = CURRENCY_EDEFAULT;

	/**
	 * The default value of the '{@link #getInputTest() <em>Input Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInputTest()
	 * @generated
	 * @ordered
	 */
	protected static final String INPUT_TEST_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getInputTest() <em>Input Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInputTest()
	 * @generated
	 * @ordered
	 */
	protected String inputTest = INPUT_TEST_EDEFAULT;

	/**
	 * The default value of the '{@link #getOutputTest() <em>Output Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutputTest()
	 * @generated
	 * @ordered
	 */
	protected static final String OUTPUT_TEST_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOutputTest() <em>Output Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutputTest()
	 * @generated
	 * @ordered
	 */
	protected String outputTest = OUTPUT_TEST_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSkusMutationOnly() <em>Skus Mutation Only</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSkusMutationOnly()
	 * @generated
	 * @ordered
	 */
	protected EList<SKU> skusMutationOnly;

	/**
	 * The cached value of the '{@link #getInputValueList() <em>Input Value List</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInputValueList()
	 * @generated
	 * @ordered
	 */
	protected EList<String> inputValueList;

	/**
	 * The cached value of the '{@link #getCurrencies() <em>Currencies</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrencies()
	 * @generated
	 * @ordered
	 */
	protected EList<Currency> currencies;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProductImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GraphQLTestPackage.Literals.PRODUCT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SKU> getSkus() {
		if (skus == null) {
			skus = new EObjectContainmentEList<SKU>(SKU.class, this, GraphQLTestPackage.PRODUCT__SKUS);
		}
		return skus;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BigInteger getSize() {
		return size;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSize(BigInteger newSize) {
		BigInteger oldSize = size;
		size = newSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GraphQLTestPackage.PRODUCT__SIZE, oldSize, size));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setActive(boolean newActive) {
		boolean oldActive = active;
		active = newActive;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GraphQLTestPackage.PRODUCT__ACTIVE, oldActive, active));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPrice(double newPrice) {
		double oldPrice = price;
		price = newPrice;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GraphQLTestPackage.PRODUCT__PRICE, oldPrice, price));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCurrency(Currency newCurrency) {
		Currency oldCurrency = currency;
		currency = newCurrency == null ? CURRENCY_EDEFAULT : newCurrency;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GraphQLTestPackage.PRODUCT__CURRENCY, oldCurrency, currency));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getInputTest() {
		return inputTest;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInputTest(String newInputTest) {
		String oldInputTest = inputTest;
		inputTest = newInputTest;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GraphQLTestPackage.PRODUCT__INPUT_TEST, oldInputTest, inputTest));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getOutputTest() {
		return outputTest;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOutputTest(String newOutputTest) {
		String oldOutputTest = outputTest;
		outputTest = newOutputTest;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GraphQLTestPackage.PRODUCT__OUTPUT_TEST, oldOutputTest, outputTest));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SKU> getSkusMutationOnly() {
		if (skusMutationOnly == null) {
			skusMutationOnly = new EObjectContainmentEList<SKU>(SKU.class, this, GraphQLTestPackage.PRODUCT__SKUS_MUTATION_ONLY);
		}
		return skusMutationOnly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getInputValueList() {
		if (inputValueList == null) {
			inputValueList = new EDataTypeUniqueEList<String>(String.class, this, GraphQLTestPackage.PRODUCT__INPUT_VALUE_LIST);
		}
		return inputValueList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Currency> getCurrencies() {
		if (currencies == null) {
			currencies = new EDataTypeUniqueEList<Currency>(Currency.class, this, GraphQLTestPackage.PRODUCT__CURRENCIES);
		}
		return currencies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case GraphQLTestPackage.PRODUCT__SKUS:
				return ((InternalEList<?>)getSkus()).basicRemove(otherEnd, msgs);
			case GraphQLTestPackage.PRODUCT__SKUS_MUTATION_ONLY:
				return ((InternalEList<?>)getSkusMutationOnly()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GraphQLTestPackage.PRODUCT__SKUS:
				return getSkus();
			case GraphQLTestPackage.PRODUCT__SIZE:
				return getSize();
			case GraphQLTestPackage.PRODUCT__ACTIVE:
				return isActive();
			case GraphQLTestPackage.PRODUCT__PRICE:
				return getPrice();
			case GraphQLTestPackage.PRODUCT__CURRENCY:
				return getCurrency();
			case GraphQLTestPackage.PRODUCT__INPUT_TEST:
				return getInputTest();
			case GraphQLTestPackage.PRODUCT__OUTPUT_TEST:
				return getOutputTest();
			case GraphQLTestPackage.PRODUCT__SKUS_MUTATION_ONLY:
				return getSkusMutationOnly();
			case GraphQLTestPackage.PRODUCT__INPUT_VALUE_LIST:
				return getInputValueList();
			case GraphQLTestPackage.PRODUCT__CURRENCIES:
				return getCurrencies();
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
			case GraphQLTestPackage.PRODUCT__SKUS:
				getSkus().clear();
				getSkus().addAll((Collection<? extends SKU>)newValue);
				return;
			case GraphQLTestPackage.PRODUCT__SIZE:
				setSize((BigInteger)newValue);
				return;
			case GraphQLTestPackage.PRODUCT__ACTIVE:
				setActive((Boolean)newValue);
				return;
			case GraphQLTestPackage.PRODUCT__PRICE:
				setPrice((Double)newValue);
				return;
			case GraphQLTestPackage.PRODUCT__CURRENCY:
				setCurrency((Currency)newValue);
				return;
			case GraphQLTestPackage.PRODUCT__INPUT_TEST:
				setInputTest((String)newValue);
				return;
			case GraphQLTestPackage.PRODUCT__OUTPUT_TEST:
				setOutputTest((String)newValue);
				return;
			case GraphQLTestPackage.PRODUCT__SKUS_MUTATION_ONLY:
				getSkusMutationOnly().clear();
				getSkusMutationOnly().addAll((Collection<? extends SKU>)newValue);
				return;
			case GraphQLTestPackage.PRODUCT__INPUT_VALUE_LIST:
				getInputValueList().clear();
				getInputValueList().addAll((Collection<? extends String>)newValue);
				return;
			case GraphQLTestPackage.PRODUCT__CURRENCIES:
				getCurrencies().clear();
				getCurrencies().addAll((Collection<? extends Currency>)newValue);
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
			case GraphQLTestPackage.PRODUCT__SKUS:
				getSkus().clear();
				return;
			case GraphQLTestPackage.PRODUCT__SIZE:
				setSize(SIZE_EDEFAULT);
				return;
			case GraphQLTestPackage.PRODUCT__ACTIVE:
				setActive(ACTIVE_EDEFAULT);
				return;
			case GraphQLTestPackage.PRODUCT__PRICE:
				setPrice(PRICE_EDEFAULT);
				return;
			case GraphQLTestPackage.PRODUCT__CURRENCY:
				setCurrency(CURRENCY_EDEFAULT);
				return;
			case GraphQLTestPackage.PRODUCT__INPUT_TEST:
				setInputTest(INPUT_TEST_EDEFAULT);
				return;
			case GraphQLTestPackage.PRODUCT__OUTPUT_TEST:
				setOutputTest(OUTPUT_TEST_EDEFAULT);
				return;
			case GraphQLTestPackage.PRODUCT__SKUS_MUTATION_ONLY:
				getSkusMutationOnly().clear();
				return;
			case GraphQLTestPackage.PRODUCT__INPUT_VALUE_LIST:
				getInputValueList().clear();
				return;
			case GraphQLTestPackage.PRODUCT__CURRENCIES:
				getCurrencies().clear();
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
			case GraphQLTestPackage.PRODUCT__SKUS:
				return skus != null && !skus.isEmpty();
			case GraphQLTestPackage.PRODUCT__SIZE:
				return SIZE_EDEFAULT == null ? size != null : !SIZE_EDEFAULT.equals(size);
			case GraphQLTestPackage.PRODUCT__ACTIVE:
				return active != ACTIVE_EDEFAULT;
			case GraphQLTestPackage.PRODUCT__PRICE:
				return price != PRICE_EDEFAULT;
			case GraphQLTestPackage.PRODUCT__CURRENCY:
				return currency != CURRENCY_EDEFAULT;
			case GraphQLTestPackage.PRODUCT__INPUT_TEST:
				return INPUT_TEST_EDEFAULT == null ? inputTest != null : !INPUT_TEST_EDEFAULT.equals(inputTest);
			case GraphQLTestPackage.PRODUCT__OUTPUT_TEST:
				return OUTPUT_TEST_EDEFAULT == null ? outputTest != null : !OUTPUT_TEST_EDEFAULT.equals(outputTest);
			case GraphQLTestPackage.PRODUCT__SKUS_MUTATION_ONLY:
				return skusMutationOnly != null && !skusMutationOnly.isEmpty();
			case GraphQLTestPackage.PRODUCT__INPUT_VALUE_LIST:
				return inputValueList != null && !inputValueList.isEmpty();
			case GraphQLTestPackage.PRODUCT__CURRENCIES:
				return currencies != null && !currencies.isEmpty();
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
		result.append(" (size: ");
		result.append(size);
		result.append(", active: ");
		result.append(active);
		result.append(", price: ");
		result.append(price);
		result.append(", currency: ");
		result.append(currency);
		result.append(", inputTest: ");
		result.append(inputTest);
		result.append(", outputTest: ");
		result.append(outputTest);
		result.append(", inputValueList: ");
		result.append(inputValueList);
		result.append(", currencies: ");
		result.append(currencies);
		result.append(')');
		return result.toString();
	}

} //ProductImpl
