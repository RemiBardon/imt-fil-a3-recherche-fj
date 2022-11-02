class FJ {
	public static void main(String[] args) {
		Program program = new Program();
	}
}

class Type extends Name {}
class Argument extends Field {}
class Parameter extends Name {}
class Assignment extends Tuple<Name, Name> {}
class ClassTable extends Array<Class> {}

class FJClass {
	Name name;
	Name superClass;
	Array<Field> fields;
	Constructor constructor;
	Array<Method> methods;
}

class Field {
	Type ftype;
	Name fname;
}

class Method {
	/**
	 * Not to be confused with mtype in [1]
	 */
	Type mtype;
	Name mname;
	Array<Argument> margs;
	Expr mexpr;
}

class Constructor {
	Name kname;
	Aray<Argument> kargs;
	Aray<Parameter> ksuperParam;
	Aray<Assignment> kassignments;
}

class Expr {}
class ExprVar extends Expr {
	Name ename;
}
class ExprField extends Expr {
	Expr eexpr;
	Name ename;
}
class ExprMethod extends Expr {
	Expr eexpr;
	Name ename;
	Array<Expr> eexprs;
}
class ExprNew extends Expr {
	Type etype;
	Array<Expr> eexprs;
}
class ExprCast extends Expr {
	Type etype;
	Expr eexpr;
}



/*

module FJ.Data where

-- {{{ Data structures and types

data Class = Class
    { cname        :: Name
    , csuperClass  :: Name
    , cfields      :: [Field]
    , cconstructor :: Constructor
    , cmethods     :: [Method]
    }
    deriving (Eq, Show)

data Field = Field
    { ftype :: Type
    , fname :: Name
    }
    deriving (Eq, Show)

data Method = Method
    { mtype :: Type -- Not to be confused with mtype in [1].
    , mname :: Name
    , margs :: [Argument]
    , mexpr :: Expr
    }
    deriving (Eq, Show)

data Constructor = Constructor
    { kname         :: Name
    , kargs         :: [Argument]
    , ksuperParam   :: [Parameter]
    , kassignments  :: [Assignment]
    }
    deriving (Eq, Show)

data Expr =
    ExprVar    { ename  :: Name }
  | ExprField  { eexpr  :: Expr
               , ename  :: Name
               }
  | ExprMethod { eexpr  :: Expr
               , ename  :: Name
               , eexprs :: [Expr]
               }
  | ExprNew    { etype  :: Type
               , eexprs :: [Expr]
               }
  | ExprCast   { etype  :: Type
               , eexpr  :: Expr
               }
    deriving (Eq, Show)

type Name        = String
type Type        = Name
type Argument    = Field
type Parameter   = Name
type Assignment  = (Name, Name)
type ClassTable  = [Class]

-- }}}

 */
