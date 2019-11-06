/**
 */
package org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Catalog Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.CatalogEntryImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.CatalogEntryImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.CatalogEntryImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.impl.CatalogEntryImpl#getCatalog <em>Catalog</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CatalogEntryImpl extends MinimalEObjectImpl.Container implements CatalogEntry {
	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getCatalog() <em>Catalog</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCatalog()
	 * @generated
	 * @ordered
	 */
	protected Catalog catalog;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CatalogEntryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GraphQLTestPackage.Literals.CATALOG_ENTRY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GraphQLTestPackage.CATALOG_ENTRY__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GraphQLTestPackage.CATALOG_ENTRY__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GraphQLTestPackage.CATALOG_ENTRY__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Catalog getCatalog() {
		if (catalog != null && catalog.eIsProxy()) {
			InternalEObject oldCatalog = (InternalEObject)catalog;
			catalog = (Catalog)eResolveProxy(oldCatalog);
			if (catalog != oldCatalog) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GraphQLTestPackage.CATALOG_ENTRY__CATALOG, oldCatalog, catalog));
			}
		}
		return catalog;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Catalog basicGetCatalog() {
		return catalog;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCatalog(Catalog newCatalog, NotificationChain msgs) {
		Catalog oldCatalog = catalog;
		catalog = newCatalog;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GraphQLTestPackage.CATALOG_ENTRY__CATALOG, oldCatalog, newCatalog);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCatalog(Catalog newCatalog) {
		if (newCatalog != catalog) {
			NotificationChain msgs = null;
			if (catalog != null)
				msgs = ((InternalEObject)catalog).eInverseRemove(this, GraphQLTestPackage.CATALOG__ENTRIES, Catalog.class, msgs);
			if (newCatalog != null)
				msgs = ((InternalEObject)newCatalog).eInverseAdd(this, GraphQLTestPackage.CATALOG__ENTRIES, Catalog.class, msgs);
			msgs = basicSetCatalog(newCatalog, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GraphQLTestPackage.CATALOG_ENTRY__CATALOG, newCatalog, newCatalog));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case GraphQLTestPackage.CATALOG_ENTRY__CATALOG:
				if (catalog != null)
					msgs = ((InternalEObject)catalog).eInverseRemove(this, GraphQLTestPackage.CATALOG__ENTRIES, Catalog.class, msgs);
				return basicSetCatalog((Catalog)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case GraphQLTestPackage.CATALOG_ENTRY__CATALOG:
				return basicSetCatalog(null, msgs);
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
			case GraphQLTestPackage.CATALOG_ENTRY__ID:
				return getId();
			case GraphQLTestPackage.CATALOG_ENTRY__NAME:
				return getName();
			case GraphQLTestPackage.CATALOG_ENTRY__DESCRIPTION:
				return getDescription();
			case GraphQLTestPackage.CATALOG_ENTRY__CATALOG:
				if (resolve) return getCatalog();
				return basicGetCatalog();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case GraphQLTestPackage.CATALOG_ENTRY__ID:
				setId((String)newValue);
				return;
			case GraphQLTestPackage.CATALOG_ENTRY__NAME:
				setName((String)newValue);
				return;
			case GraphQLTestPackage.CATALOG_ENTRY__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case GraphQLTestPackage.CATALOG_ENTRY__CATALOG:
				setCatalog((Catalog)newValue);
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
			case GraphQLTestPackage.CATALOG_ENTRY__ID:
				setId(ID_EDEFAULT);
				return;
			case GraphQLTestPackage.CATALOG_ENTRY__NAME:
				setName(NAME_EDEFAULT);
				return;
			case GraphQLTestPackage.CATALOG_ENTRY__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case GraphQLTestPackage.CATALOG_ENTRY__CATALOG:
				setCatalog((Catalog)null);
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
			case GraphQLTestPackage.CATALOG_ENTRY__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case GraphQLTestPackage.CATALOG_ENTRY__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case GraphQLTestPackage.CATALOG_ENTRY__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case GraphQLTestPackage.CATALOG_ENTRY__CATALOG:
				return catalog != null;
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
		result.append(" (id: ");
		result.append(id);
		result.append(", name: ");
		result.append(name);
		result.append(", description: ");
		result.append(description);
		result.append(')');
		return result.toString();
	}

} //CatalogEntryImpl
