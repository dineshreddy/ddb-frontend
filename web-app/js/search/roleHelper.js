/*
 * Copyright (C) 2014 FIZ Karlsruhe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Search namespace  */
de.ddb.next.search = de.ddb.next.search || {};

//This patter searches for _1_ which is the devider between literal and role
de.ddb.next.search.literalDeviderPattern = /_[0-9]+_/;

/**
 * Returns a role without its literal part. 
 * 
 * Returns the literal part of a role 
 * 
 * If a concrete role looks like this 
 * "Cotta_1_affiliate_fct_involved"
 * 
 * The method return 
 * "_1_affiliate_fct_involved"
 * 
 * Roles have the following structure:
 * <Wert des Literals>_<Stufe der Hierarchie der Rolle>_<Name der Facette>_<Name der ersten Rollen-Ebene>_..._<Name der n-ten Rollen-Ebene> 
 * 
 * If the devider pattern does not match, return <code>null</code>
 */
de.ddb.next.search.getRoleWithoutLiteral = function(role) {
//  console.log("getRoleWithoutLiteral " + role);
  
  var currObjInstance = this;
  var roleWithoutLiteral = null;
  
  //Check if the role value matches the pattern
  var devider = de.ddb.next.search.literalDeviderPattern.exec(role);
  if (devider) {
    var indexOfDevider = role.indexOf(devider); 
    roleWithoutLiteral = role.substring(indexOfDevider);
  }
  
  return roleWithoutLiteral;
};

/**
 * Returns the literal part of a role 
 * 
 * If a concrete role looks like this 
 * "Cotta_1_affiliate_fct_involved"
 * 
 * The method return 
 * "Cotta"
 * 
 * Roles have the following structure:
 * <Wert des Literals>_<Stufe der Hierarchie der Rolle>_<Name der Facette>_<Name der ersten Rollen-Ebene>_..._<Name der n-ten Rollen-Ebene> 
 * 
 * If the devider pattern does not match, return <code>null</code>
 */
de.ddb.next.search.getLiteralFromRole = function(role) {
  var currObjInstance = this;
  
  var literal = null;
  
  //Check if the role value matches the pattern
  var devider = de.ddb.next.search.literalDeviderPattern.exec(role);
  if (devider) {
    var split = role.split(devider); 
//    console.log("split " + split);
    literal = split[0];
  }
//  console.log("literal: " + literal)
  return literal;
};

/**
 * Returns the role without literal and hierarchie number
 * 
 * If a concrete role looks like this 
 * "Cotta_1_affiliate_fct_involved"
 * 
 * The method return 
 * "affiliate_fct_involved"
 * 
 * Roles have the following structure:
 * <Wert des Literals>_<Stufe der Hierarchie der Rolle>_<Name der Facette>_<Name der ersten Rollen-Ebene>_..._<Name der n-ten Rollen-Ebene> 
 * 
 * If the devider pattern does not match, return <code>null</code>
 */
de.ddb.next.search.getRoleWithoutLiteralAndHierarchieNumber = function(role) {
  var currObjInstance = this;
  
  var splitedRole = null;
  
  //Check if the role value matches the pattern
  var devider = de.ddb.next.search.literalDeviderPattern.exec(role);
  if (devider) {
    var split = role.split(devider); 
    console.log("split " + split);
    splitedRole = split[1];
  }
  console.log("splitedRole: " + splitedRole)
  return splitedRole;
};