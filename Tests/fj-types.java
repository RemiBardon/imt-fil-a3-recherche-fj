import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class FJ {
	public static void main(String[] args) {
		List<String> emptyStringList = Arrays.asList();
		List<FJField> emptyFJFieldList = Arrays.asList();
		List<FJMethod> emptyFJMethodList = Arrays.asList();
		HashMap<String,String> emptyHashMap = new HashMap<String,String>();

		FJClass objectClass = new FJClass("Object",null,null,emptyFJMethodList,emptyFJFieldList);
		//Class A
		FJConstructor constructorA = new FJConstructor(emptyFJFieldList,emptyStringList,emptyHashMap);
		FJClass classA = new FJClass("A",objectClass,constructorA,emptyFJMethodList,emptyFJFieldList);

		//Class B
		FJConstructor constructorB = new FJConstructor(emptyFJFieldList,emptyStringList,emptyHashMap);
		FJClass classB = new FJClass("B",objectClass,constructorB,emptyFJMethodList,emptyFJFieldList);

		//Pair
		FJField fst = new FJField("fst",objectClass);
		FJField snd = new FJField("snd",objectClass);
		HashMap<String,String> pairHashMap = new HashMap<String,String>();

		pairHashMap.put("fst","fst");
		pairHashMap.put("snd","snd");

		FJConstructor constructorPair = new FJConstructor(Arrays.asList(fst,snd),emptyStringList,pairHashMap);

		//Prolème ici, la méthode doit retourner un Pair, mais la class n'est pas encore défini
		FJMethod pairFst = new FJMethod("setfst",objectClass,emptyFJFieldList,emptyStringList,emptyHashMap);


		FJProgram program = new FJProgram(Arrays.asList(classA,classB));
	}
}


class FJProgram {

	public FJProgram(List<FJClass> classes) {
		this.classes = classes;
	}

	private List<FJClass> classes;
}

class FJClass extends Type {

	public FJClass(String name, FJClass superClass,FJConstructor constructor, List<FJMethod> methods,List<FJField> fields) {
		super(name);
		this.superClass = superClass;
		this.constructor = constructor;
		this.methods = methods;
		this.fields = fields;
	}
	private FJClass superClass;
	private List<FJField> fields;
	private List<FJMethod> methods;
	private FJConstructor constructor;

	@Override
	public String toString() {
		return "class " + name + " extends " + superClass + "/n"
				 + "fields: " + fields + "/n" +
				 "methods: " + methods + "/n" +
				 "constructor: " + constructor;
	}
}

@Setter
@Getter
@AllArgsConstructor
class FJField {
	private String name;
	private Type type;
}


class FJMethod {

	public FJMethod(String name, Type returnType, List<FJField> args,Expression expression) {
		this.name = name;
		this.returnType = returnType;
		this.args = args;
		this.expression = expression;
	}

	private String name;
	private Type returnType;
	private List<FJField> args; //Argument meme chose que FJField
	private Expression expression;
}


class FJConstructor {
	public FJConstructor(List<FJField> args, List<String> superParams, HashMap<String,String> assignments) {
		this.args = args;
		this.superParams = superParams;
		this.assignments = assignments;
	}

	private List<FJField> args;
	private List<String> superParams;
	private HashMap<String,String> assignments = new HashMap<>();

}

@Setter
@Getter
@AllArgsConstructor
class Type {
	protected String name;
}

@Setter
@Getter
class Expression {}

@Setter
@Getter
class ExprVar extends Expression {
	private String name;
}

@Setter
@Getter
class ExprField  extends Expression {
	private Expression expression;
	private String name;
}

@Setter
@Getter
class ExprMethod extends Expression {
	private Expression expression;
	private String name;
	private List<Expression> args;
}

@Setter
@Getter
class ExprNew extends Expression {
	private FJClass type;
	private List<Expression> args;
}

@Setter
@Getter
class ExprCast extends Expression {
	private FJClass type;
	private Expression expression;
}