/**
 */
package org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/OCL/Import ecore='http://www.eclipse.org/emf/2002/Ecore'"
 * @generated
 */
public interface GraphQLTestPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "GraphqlTest";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://data-in-motion.biz/graphql/test/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "graphqltest";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GraphQLTestPackage eINSTANCE = org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.CatalogImpl <em>Catalog</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.CatalogImpl
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl#getCatalog()
	 * @generated
	 */
	int CATALOG = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATALOG__ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATALOG__NAME = 1;

	/**
	 * The feature id for the '<em><b>Entries</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATALOG__ENTRIES = 2;

	/**
	 * The number of structural features of the '<em>Catalog</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATALOG_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Catalog</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATALOG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.CatalogEntryImpl <em>Catalog Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.CatalogEntryImpl
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl#getCatalogEntry()
	 * @generated
	 */
	int CATALOG_ENTRY = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATALOG_ENTRY__ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATALOG_ENTRY__NAME = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATALOG_ENTRY__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>Catalog</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATALOG_ENTRY__CATALOG = 3;

	/**
	 * The number of structural features of the '<em>Catalog Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATALOG_ENTRY_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Catalog Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATALOG_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.ProductImpl <em>Product</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.ProductImpl
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl#getProduct()
	 * @generated
	 */
	int PRODUCT = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__ID = CATALOG_ENTRY__ID;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__NAME = CATALOG_ENTRY__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__DESCRIPTION = CATALOG_ENTRY__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Catalog</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__CATALOG = CATALOG_ENTRY__CATALOG;

	/**
	 * The feature id for the '<em><b>Skus</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__SKUS = CATALOG_ENTRY_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__SIZE = CATALOG_ENTRY_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__ACTIVE = CATALOG_ENTRY_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__PRICE = CATALOG_ENTRY_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Currency</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__CURRENCY = CATALOG_ENTRY_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Input Test</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__INPUT_TEST = CATALOG_ENTRY_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Output Test</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__OUTPUT_TEST = CATALOG_ENTRY_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Skus Mutation Only</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__SKUS_MUTATION_ONLY = CATALOG_ENTRY_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Input Value List</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__INPUT_VALUE_LIST = CATALOG_ENTRY_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Currencies</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT__CURRENCIES = CATALOG_ENTRY_FEATURE_COUNT + 9;

	/**
	 * The number of structural features of the '<em>Product</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT_FEATURE_COUNT = CATALOG_ENTRY_FEATURE_COUNT + 10;

	/**
	 * The number of operations of the '<em>Product</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRODUCT_OPERATION_COUNT = CATALOG_ENTRY_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.SKUImpl <em>SKU</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.SKUImpl
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl#getSKU()
	 * @generated
	 */
	int SKU = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SKU__ID = 0;

	/**
	 * The number of structural features of the '<em>SKU</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SKU_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>SKU</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SKU_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.DataFetcherTestImpl <em>Data Fetcher Test</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.DataFetcherTestImpl
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl#getDataFetcherTest()
	 * @generated
	 */
	int DATA_FETCHER_TEST = 4;

	/**
	 * The feature id for the '<em><b>Test</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_FETCHER_TEST__TEST = 0;

	/**
	 * The feature id for the '<em><b>Skus</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_FETCHER_TEST__SKUS = 1;

	/**
	 * The number of structural features of the '<em>Data Fetcher Test</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_FETCHER_TEST_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Data Fetcher Test</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_FETCHER_TEST_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency <em>Currency</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl#getCurrency()
	 * @generated
	 */
	int CURRENCY = 5;


	/**
	 * Returns the meta object for class '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Catalog <em>Catalog</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Catalog</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Catalog
	 * @generated
	 */
	EClass getCatalog();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Catalog#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Catalog#getId()
	 * @see #getCatalog()
	 * @generated
	 */
	EAttribute getCatalog_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Catalog#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Catalog#getName()
	 * @see #getCatalog()
	 * @generated
	 */
	EAttribute getCatalog_Name();

	/**
	 * Returns the meta object for the reference list '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Catalog#getEntries <em>Entries</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Entries</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Catalog#getEntries()
	 * @see #getCatalog()
	 * @generated
	 */
	EReference getCatalog_Entries();

	/**
	 * Returns the meta object for class '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.CatalogEntry <em>Catalog Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Catalog Entry</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.CatalogEntry
	 * @generated
	 */
	EClass getCatalogEntry();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.CatalogEntry#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.CatalogEntry#getId()
	 * @see #getCatalogEntry()
	 * @generated
	 */
	EAttribute getCatalogEntry_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.CatalogEntry#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.CatalogEntry#getName()
	 * @see #getCatalogEntry()
	 * @generated
	 */
	EAttribute getCatalogEntry_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.CatalogEntry#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.CatalogEntry#getDescription()
	 * @see #getCatalogEntry()
	 * @generated
	 */
	EAttribute getCatalogEntry_Description();

	/**
	 * Returns the meta object for the reference '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.CatalogEntry#getCatalog <em>Catalog</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Catalog</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.CatalogEntry#getCatalog()
	 * @see #getCatalogEntry()
	 * @generated
	 */
	EReference getCatalogEntry_Catalog();

	/**
	 * Returns the meta object for class '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product <em>Product</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Product</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product
	 * @generated
	 */
	EClass getProduct();

	/**
	 * Returns the meta object for the containment reference list '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getSkus <em>Skus</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Skus</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getSkus()
	 * @see #getProduct()
	 * @generated
	 */
	EReference getProduct_Skus();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getSize <em>Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Size</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getSize()
	 * @see #getProduct()
	 * @generated
	 */
	EAttribute getProduct_Size();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#isActive <em>Active</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Active</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#isActive()
	 * @see #getProduct()
	 * @generated
	 */
	EAttribute getProduct_Active();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getPrice <em>Price</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Price</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getPrice()
	 * @see #getProduct()
	 * @generated
	 */
	EAttribute getProduct_Price();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getCurrency <em>Currency</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Currency</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getCurrency()
	 * @see #getProduct()
	 * @generated
	 */
	EAttribute getProduct_Currency();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getInputTest <em>Input Test</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Input Test</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getInputTest()
	 * @see #getProduct()
	 * @generated
	 */
	EAttribute getProduct_InputTest();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getOutputTest <em>Output Test</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Output Test</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getOutputTest()
	 * @see #getProduct()
	 * @generated
	 */
	EAttribute getProduct_OutputTest();

	/**
	 * Returns the meta object for the containment reference list '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getSkusMutationOnly <em>Skus Mutation Only</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Skus Mutation Only</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getSkusMutationOnly()
	 * @see #getProduct()
	 * @generated
	 */
	EReference getProduct_SkusMutationOnly();

	/**
	 * Returns the meta object for the attribute list '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getInputValueList <em>Input Value List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Input Value List</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getInputValueList()
	 * @see #getProduct()
	 * @generated
	 */
	EAttribute getProduct_InputValueList();

	/**
	 * Returns the meta object for the attribute list '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getCurrencies <em>Currencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Currencies</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getCurrencies()
	 * @see #getProduct()
	 * @generated
	 */
	EAttribute getProduct_Currencies();

	/**
	 * Returns the meta object for class '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.SKU <em>SKU</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>SKU</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.SKU
	 * @generated
	 */
	EClass getSKU();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.SKU#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.SKU#getId()
	 * @see #getSKU()
	 * @generated
	 */
	EAttribute getSKU_Id();

	/**
	 * Returns the meta object for class '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.DataFetcherTest <em>Data Fetcher Test</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Fetcher Test</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.DataFetcherTest
	 * @generated
	 */
	EClass getDataFetcherTest();

	/**
	 * Returns the meta object for the attribute '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.DataFetcherTest#getTest <em>Test</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Test</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.DataFetcherTest#getTest()
	 * @see #getDataFetcherTest()
	 * @generated
	 */
	EAttribute getDataFetcherTest_Test();

	/**
	 * Returns the meta object for the reference list '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.DataFetcherTest#getSkus <em>Skus</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Skus</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.DataFetcherTest#getSkus()
	 * @see #getDataFetcherTest()
	 * @generated
	 */
	EReference getDataFetcherTest_Skus();

	/**
	 * Returns the meta object for enum '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency <em>Currency</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Currency</em>'.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency
	 * @generated
	 */
	EEnum getCurrency();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	GraphQLTestFactory getGraphQLTestFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.CatalogImpl <em>Catalog</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.CatalogImpl
		 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl#getCatalog()
		 * @generated
		 */
		EClass CATALOG = eINSTANCE.getCatalog();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CATALOG__ID = eINSTANCE.getCatalog_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CATALOG__NAME = eINSTANCE.getCatalog_Name();

		/**
		 * The meta object literal for the '<em><b>Entries</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CATALOG__ENTRIES = eINSTANCE.getCatalog_Entries();

		/**
		 * The meta object literal for the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.CatalogEntryImpl <em>Catalog Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.CatalogEntryImpl
		 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl#getCatalogEntry()
		 * @generated
		 */
		EClass CATALOG_ENTRY = eINSTANCE.getCatalogEntry();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CATALOG_ENTRY__ID = eINSTANCE.getCatalogEntry_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CATALOG_ENTRY__NAME = eINSTANCE.getCatalogEntry_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CATALOG_ENTRY__DESCRIPTION = eINSTANCE.getCatalogEntry_Description();

		/**
		 * The meta object literal for the '<em><b>Catalog</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CATALOG_ENTRY__CATALOG = eINSTANCE.getCatalogEntry_Catalog();

		/**
		 * The meta object literal for the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.ProductImpl <em>Product</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.ProductImpl
		 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl#getProduct()
		 * @generated
		 */
		EClass PRODUCT = eINSTANCE.getProduct();

		/**
		 * The meta object literal for the '<em><b>Skus</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PRODUCT__SKUS = eINSTANCE.getProduct_Skus();

		/**
		 * The meta object literal for the '<em><b>Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PRODUCT__SIZE = eINSTANCE.getProduct_Size();

		/**
		 * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PRODUCT__ACTIVE = eINSTANCE.getProduct_Active();

		/**
		 * The meta object literal for the '<em><b>Price</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PRODUCT__PRICE = eINSTANCE.getProduct_Price();

		/**
		 * The meta object literal for the '<em><b>Currency</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PRODUCT__CURRENCY = eINSTANCE.getProduct_Currency();

		/**
		 * The meta object literal for the '<em><b>Input Test</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PRODUCT__INPUT_TEST = eINSTANCE.getProduct_InputTest();

		/**
		 * The meta object literal for the '<em><b>Output Test</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PRODUCT__OUTPUT_TEST = eINSTANCE.getProduct_OutputTest();

		/**
		 * The meta object literal for the '<em><b>Skus Mutation Only</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PRODUCT__SKUS_MUTATION_ONLY = eINSTANCE.getProduct_SkusMutationOnly();

		/**
		 * The meta object literal for the '<em><b>Input Value List</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PRODUCT__INPUT_VALUE_LIST = eINSTANCE.getProduct_InputValueList();

		/**
		 * The meta object literal for the '<em><b>Currencies</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PRODUCT__CURRENCIES = eINSTANCE.getProduct_Currencies();

		/**
		 * The meta object literal for the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.SKUImpl <em>SKU</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.SKUImpl
		 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl#getSKU()
		 * @generated
		 */
		EClass SKU = eINSTANCE.getSKU();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SKU__ID = eINSTANCE.getSKU_Id();

		/**
		 * The meta object literal for the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.DataFetcherTestImpl <em>Data Fetcher Test</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.DataFetcherTestImpl
		 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl#getDataFetcherTest()
		 * @generated
		 */
		EClass DATA_FETCHER_TEST = eINSTANCE.getDataFetcherTest();

		/**
		 * The meta object literal for the '<em><b>Test</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_FETCHER_TEST__TEST = eINSTANCE.getDataFetcherTest_Test();

		/**
		 * The meta object literal for the '<em><b>Skus</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_FETCHER_TEST__SKUS = eINSTANCE.getDataFetcherTest_Skus();

		/**
		 * The meta object literal for the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency <em>Currency</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency
		 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestPackageImpl#getCurrency()
		 * @generated
		 */
		EEnum CURRENCY = eINSTANCE.getCurrency();

	}

} //GraphQLTestPackage
