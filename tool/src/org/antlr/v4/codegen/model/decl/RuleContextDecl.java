/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

/** */
public class RuleContextDecl extends Decl {
	public String ctxName;
	public boolean isImplicit;
	public boolean isImported;
	public String prefix;

	public RuleContextDecl(OutputModelFactory factory, String name, String ctxName, String prefix, boolean imported) {
		super(factory, name);
		this.ctxName = ctxName;
		this.isImported = imported;
		this.prefix = prefix;
	}
}
