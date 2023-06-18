package cc.fxqq.hippo.consts;

/**
 *
 */
public enum ComplexOrderRelationEnum {
	PARENT(1),
	CHILDREN(0);
	
	private Integer value;
	
	private ComplexOrderRelationEnum(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
}
