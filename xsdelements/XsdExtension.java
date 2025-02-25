﻿package proholz.xsdparser;

/**
 * A class representing the xsd:extension element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_extension.asp">xsd:extension description and usage at w3c</a>
 */
public class XsdExtension extends XsdAnnotatedElements {

	public static final String XSD_TAG = "xsd:extension";
	public static final String XS_TAG = "xs:extension";

	/**
	 * {@link XsdExtensionVisitor} instance which restricts the children to {@link XsdGroup} and
	 * {@link XsdMultipleElements} instances.
	 * Can also have {@link XsdAttribute} and {@link XsdAttributeGroup} elements as children as per inheritance of
	 * {@link AttributesVisitor}.
	 * Can also have {@link XsdAnnotation} as children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
	 */
	private XsdExtensionVisitor visitor = new XsdExtensionVisitor(this);

	/**
	 * The child element of the {@link XsdExtension} instance. Either a {@link XsdGroup}, {@link XsdAll},
	 * {@link XsdSequence} or a {@link XsdChoice} instance wrapped in a {@link ReferenceBase} object.
	 */
	private ReferenceBase childElement;

	/**
	 * A {@link XsdElement} instance wrapped in a {@link ReferenceBase} object from which this {@link XsdExtension}
	 * instance extends.
	 */
	private ReferenceBase base;

	private XsdExtension(XsdParserCore! parser, Dictionary<String, String>! attributesMap) {
		super(parser, attributesMap);

		String baseValue = attributesMap.getOrDefault(BASE_TAG, null);

		if (baseValue != null){
			if (XsdParserCore.getXsdTypesToCodeGen().ContainsKey(baseValue)){
				Dictionary<String, String> attributes = new Dictionary<String, String>();
				attributes.Add(NAME_TAG, baseValue);
				this.base = ReferenceBase.createFromXsd(new XsdComplexType(this, this.parser, attributes));
			} else {
				this.base = new UnsolvedReference(baseValue, new XsdElement(this, this.parser, new Dictionary<String, String>()));
				parser.addUnsolvedReference((UnsolvedReference) this.base);
			}
		}
	}

	/**
	 * This method should always receive two elements, one to replace the {@link UnsolvedReference} created due to
	 * the value present in the base attribute and another if it has an {@link UnsolvedReference} as a child element.
	 * @param element A concrete element with a name that will replace the {@link UnsolvedReference} object created in the
	 *                {@link XsdExtension} constructor. The {@link UnsolvedReference} is only replaced if there
	 *                is a match between the {@link UnsolvedReference#ref} and the {@link NamedConcreteElement#name}.
	 */
	@Override
	public void replaceUnsolvedElements(NamedConcreteElement element) {
		super.replaceUnsolvedElements(element);

		XsdNamedElements elem = (XsdNamedElements) element.getElement();
	   // String elemName = elem.getRawName();

		boolean isComplexOrSimpleType = elem instanceof XsdComplexType || elem instanceof XsdSimpleType;

		if (this.base instanceof UnsolvedReference && isComplexOrSimpleType && compareReference(element, (UnsolvedReference) this.base)){
			this.base = element;
		}

		if (this.childElement instanceof UnsolvedReference &&
				elem instanceof XsdGroup && compareReference(element, (UnsolvedReference) this.childElement)){
			this.childElement = element;
		}

		visitor.replaceUnsolvedAttributes(element);
	}

	@Override
	public XsdAbstractElementVisitor getVisitor() {
		return visitor;
	}

	@Override
	public void accept(XsdAbstractElementVisitor visitorParam) {
		super.accept(visitorParam);
		visitorParam.visit(this);
	}

	/**
	 * @return Its children elements as his own.
	 */
	@Override
	public List<ReferenceBase> getElements() {
		return childElement == null ? new List<ReferenceBase>() : childElement.getElement().getElements();
	}

	/**
	 * @return Either a {@link XsdComplexType} or a {@link XsdSimpleType} from which this extension extends or null if
	 * the {@link XsdParserCore} wasn't able to replace the {@link UnsolvedReference} created by the base attribute value.
	 */
	public XsdNamedElements getBase() {
		if (base instanceof NamedConcreteElement){
			return
			//(NamedConcreteElement)
			(XsdNamedElements) base.getElement();
		}

		return null;
	}

	/**
	 * @return The {@link XsdComplexType} from which this extension extends or null if the {@link XsdParserCore} wasn't
	 * able to replace the {@link UnsolvedReference} created by the base attribute value.
	 */
	public XsdComplexType getBaseAsComplexType() {
		if (base instanceof NamedConcreteElement){
			XsdAbstractElement baseType = base.getElement();

			if (baseType instanceof XsdComplexType){
				return (XsdComplexType) baseType;
			}
		}

		return null;
	}

	/**
	 * @return The {@link XsdSimpleType} from which this extension extends or null if the {@link XsdParserCore} wasn't
	 * able to replace the {@link UnsolvedReference} created by the base attribute value.
	 */
	//@SuppressWarnings("unused")
	public XsdSimpleType getBaseAsSimpleType() {
		if (base instanceof NamedConcreteElement){
			XsdAbstractElement baseType = base.getElement();

			if (baseType instanceof XsdSimpleType){
				return (XsdSimpleType) baseType;
			}
		}

		return null;
	}

	public static ReferenceBase parse(XsdParserCore! parser, XmlElement node){
		return xsdParseSkeleton(node, new XsdExtension(parser, convertNodeMap(node.Attributes)));
	}

	//@SuppressWarnings("unused")
	public ISequence<XsdAttribute> getXsdAttributes() {
		return visitor.getXsdAttributes();
	}

	//@SuppressWarnings("unused")
	public ISequence<XsdAttributeGroup> getXsdAttributeGroup() {
		return visitor.getXsdAttributeGroup();
	}

	//@SuppressWarnings("unused")
	public XsdAbstractElement getXsdChildElement(){
		if (childElement == null) {
			return null;
		}

		return (childElement instanceof UnsolvedReference) ? null : childElement.getElement();
	}

	public void setChildElement(ReferenceBase childElement) {
		this.childElement = childElement;
	}

	/**
	 * @return The childElement as a {@link XsdGroup} object or null if childElement isn't a {@link XsdGroup} instance.
	 */
	//@SuppressWarnings("unused")
	public XsdGroup getChildAsGroup() {
		return (childElement.getElement() instanceof XsdGroup) ? (XsdGroup) childElement.getElement() : null;
	}

	/**
	 * @return The childElement as a {@link XsdAll} object or null if childElement isn't a {@link XsdAll} instance.
	 */
	//@SuppressWarnings("unused")
	public XsdAll getChildAsAll() {
		return childrenIsMultipleElement() ? XsdMultipleElements.getChildAsdAll((XsdMultipleElements) childElement.getElement()) : null;
	}

	/**
	 * @return The childElement as a {@link XsdChoice} object or null if childElement isn't a {@link XsdChoice} instance.
	 */
	//@SuppressWarnings("unused")
	public XsdChoice getChildAsChoice() {
		return childrenIsMultipleElement() ? XsdMultipleElements.getChildAsChoice((XsdMultipleElements) childElement.getElement()) : null;
	}

	/**
	 * @return The childElement as a {@link XsdSequence} object or null if childElement isn't a {@link XsdSequence} instance.
	 */
	//@SuppressWarnings("unused")
	public XsdSequence getChildAsSequence() {
		return childrenIsMultipleElement() ? XsdMultipleElements.getChildAsSequence((XsdMultipleElements) childElement.getElement()) : null;
	}

	private boolean childrenIsMultipleElement(){
	   // Boolean res = childElement.getElement() instanceof XsdMultipleElements;
		return childElement.getElement() instanceof XsdMultipleElements;
	}
}