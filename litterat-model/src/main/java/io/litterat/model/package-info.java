/*
 * Copyright (c) 2020-2021, Live Media Pty. Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.litterat.model;
/**
 * 
 * @formatter:off
 * 
 * definition: record( [ field( "name", type_name, true ), 
 *    					 field( "definition", element, true) ] );
 * 
 * 
 * element: union( [record, array, union] );
 * record: record( [ field( "fields", array( field ), true ) ] ); 
 * array: record( [ field( "type", type_name, true ) ] ); 
 * field: record( [ field( "name", identifier, true ), 
 *                  field( "type", type_name, true ), 
 *                  field( "required", boolean, true ) ] );
 * 
 * ## Atom definitions need more work. 
 * atom: record( [ field( "attributes", array( atom_attributes ), true ) ] ); 
 * atom_attributes: union( encoding, integer_attributes, float_attributes );
 * integer_attributes: union( atom_integer, atom_bigendian, atom_littleendian, atom_signed, atom_unsigned ) 
 * type_name: identifier; 
 * identifier: string; 
 * string: ## todo is this an atom or array?
 * 
 * 
 * @formatter:on
 * 
 */
