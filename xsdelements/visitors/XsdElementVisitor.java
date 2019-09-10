﻿package proholz.xsdparser;

/**
 * Represents the restrictions of the {@link XsdElement} element, which can only contain {@link XsdSimpleType} or
 * {@link XsdComplexType} as children. Can also have {@link XsdAnnotation} children as per inheritance of
 * {@link XsdAnnotatedElementsVisitor}.
 */
public class XsdElementVisitor extends XsdAnnotatedElementsVisitor {

	/**
	 * The {@link XsdElement} instance which owns this {@link XsdElementVisitor} instance. This way this visitor instance
	 * can perform changes in the {@link XsdElement} object.
	 */
	private final XsdElement owner;

	public XsdElementVisitor(XsdElement owner) {
		super(owner);
		this.owner = owner;
	}



	@Override
	public void visit(XsdComplexType element) {
		super.visit(element);

		owner.setComplexType(ReferenceBase.createFromXsd(element));
	}

	@Override
	public void visit(XsdSimpleType element) {
		super.visit(element);

		owner.setSimpleType(ReferenceBase.createFromXsd(element));
	}
}