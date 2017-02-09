/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A model object representing a parse tree listener file.
 *  These are the rules specific events triggered by a parse tree visitor.
 */
public class ListenerFile extends OutputFile {
	public String genPackage; // from -package cmd-line
	public String antlrRuntimeImport; // from -runtimeImport cmd-line
	public String addImport; // from -addImport cmd-line
	public String exportMacro; // from -DexportMacro cmd-line
	public String grammarName;
	public String parserName;
	/**
	 * The names of all listener contexts.
	 */
	public Set<String> listenerNames = new LinkedHashSet<String>();

	public Set<String> listenerNamesImported = new LinkedHashSet<String>();
	public Set<String> listenerNamesLocal = new LinkedHashSet<String>();
	
	/**
	 * For listener contexts created for a labeled outer alternative, maps from
	 * a listener context name to the name of the rule which defines the
	 * context.
	 */
	public Map<String, String> listenerLabelRuleNames = new LinkedHashMap<String, String>();

	@ModelElement public Action header;
	@ModelElement public Map<String, Action> namedActions;

	public ListenerFile(OutputModelFactory factory, String fileName) {
		super(factory, fileName);
		Grammar g = factory.getGrammar();
		parserName = g.getRecognizerName();
		grammarName = g.name;
		namedActions = buildNamedActions(factory.getGrammar());
		for (Rule r : g.rules.values()) {
//			System.out.println("ListenerFile " +  r.g.name.equals( r.importedG.name) + " \t" + r.name + " " + r.g.name + " " + r.importedG.name);
			
			Map<String, List<Pair<Integer,AltAST>>> labels = r.getAltLabels();
			if ( labels!=null ) {
				for (Map.Entry<String, List<Pair<Integer, AltAST>>> pair : labels.entrySet()) {
					String k = pair.getKey();
					List<Pair<Integer, AltAST>> v = pair.getValue();
					listenerNames.add(pair.getKey());
					listenerLabelRuleNames.put(pair.getKey(), r.name);
					if( r.imported ){
						listenerNamesLocal.add(pair.getKey());
					} else {
						listenerNamesImported.add(pair.getKey());
					}
				}
			}
			else {
				// only add rule context if no labels
				listenerNames.add(r.name);
				if( r.imported ){
					listenerNamesLocal.add(r.name);
				} else {
					listenerNamesImported.add(r.name);
				}
			}
		}
		ActionAST ast = g.namedActions.get("header");
		if ( ast!=null ) header = new Action(factory, ast);
		genPackage = factory.getGrammar().tool.genPackage;
		antlrRuntimeImport = factory.getGrammar().tool.antlrRuntimeImport;
		
		if ( factory.getGrammar().tool.addImports.size() > 0 ) {
			Iterator<String> iter = factory.getGrammar().tool.addImports.iterator();
			addImport = iter.next(); 
			if ( iter.hasNext() ) {
				factory.getGrammar().tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, "multiple addImport not implemented");
			}
		}
		exportMacro = factory.getGrammar().getOptionString("exportMacro");
	}
}
