package handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import struct.Lat_Attribute;
import struct.Lat_Concept;
import struct.Lat_Object;
import struct.Lattice;

public class LatticeHandler {

	public ArrayList<Lat_Attribute> intersectionIntent(ArrayList<Lat_Attribute> intentOld, ArrayList<Lat_Attribute> intentNew){
		
		ArrayList<Lat_Attribute> intent = new ArrayList<Lat_Attribute>();
		
		for (int i=0; i<intentNew.size(); i++){
			for (int j=0; j<intentOld.size(); j++){
				if (intentOld.get(j).id == intentNew.get(i).id)
					intent.add(intentNew.get(i));
			}
		}
		
		return intent;
		
	}
	
	public ArrayList<Lat_Object> unionExtent(ArrayList<Lat_Object> extentOld, ArrayList<Lat_Object> extentNew){
		
		ArrayList<Lat_Object> extent = new ArrayList<Lat_Object>();
		
		for (int i=0; i<extentNew.size(); i++){
				extent.add(extentNew.get(i));
		}
		
		for (int i=0; i<extentOld.size(); i++){
			extent.add(extentOld.get(i));
		}
		
		return extent;
		
	}

	public ArrayList<Lat_Attribute> computeGenerators(ArrayList<Lat_Attribute> generator, ArrayList<Lat_Attribute> intent){
		
		ArrayList<Lat_Attribute> gen = new ArrayList<Lat_Attribute>();
		
		for (int i=0; i<intent.size(); i++){
			for (int j=0; j<generator.size(); j++){
				if (generator.get(j).id == intent.get(i).id)
					gen.add(intent.get(i));
			}
		}
		
		return gen;
		
	}
	
	public Lattice UpdateLattice(Lattice lat, Lattice lat_temp){
		
		Lattice lattice = lat;
		
		//System.out.println("Lattice nb concepts = "+ lattice.concepts.size());
		//System.out.println("Lattice temp nb concepts = "+ lat_temp.concepts.size());
		ArrayList<Lat_Object> newObjList = new ArrayList<Lat_Object>();
		
		for (Map.Entry<Integer, Lat_Concept> c : lat.concepts.entrySet()){
			ArrayList<Lat_Attribute> intentOld = c.getValue().intent;
			for (Map.Entry<Integer, Lat_Concept> ct : lat_temp.concepts.entrySet()){
				ArrayList<Lat_Attribute> intentNew = ct.getValue().intent;
				int cnt = 0;
				
				//System.out.println("\n");
				for (int i=0; i<intentNew.size(); i++){
					//System.out.println(intentNew.get(i));
					for (int j=0; j<intentOld.size(); j++){
						// Verifier si l'intersection des intents est non vide
						//System.out.print("\t");
						//System.out.println(intentOld.get(j));
						if (intentOld.get(j).id == intentNew.get(i).id){
							cnt ++;
						}
					}
				}
			
				//System.out.println("cnt = "+ cnt);
				// Fusion des concepts
				if (cnt != 0 ) {
					c.getValue().intent = intersectionIntent(intentOld, intentNew);
					if (!c.getValue().extent.isEmpty())
						c.getValue().extent = unionExtent(c.getValue().extent, ct.getValue().extent);
					if (!c.getValue().generators.isEmpty())
						c.getValue().generators = computeGenerators(c.getValue().generators, ct.getValue().intent);
				}
				
				// Mettre a jour la liste des objets rajoutes
				for (int i=0; i< ct.getValue().extent.size(); i++){
					if (c.getValue().extent.contains(ct.getValue().extent.get(i))){
						if (!newObjList.contains(ct.getValue().extent.get(i)))
							newObjList.add(ct.getValue().extent.get(i));
					}
				} 
				
			}
		}
		
		// Verifier s'il existe des objets non rajoutes dans le lattice
		for (Map.Entry<Integer, Lat_Object> obj : lat_temp.objects.entrySet()){
			if (!newObjList.contains(obj.getValue())){
				//System.out.println("La regle " + obj.getValue().name + " n'existe pas dans le lattice");
			}
		}
		
		return lattice;
		
	}
	
	public Lattice ReadLatticeXML (String LatticeXMLFileName, int LatticeId, String latticeName, int LatticeMinSupport, int idOffset) {
		
		Lattice lat = new Lattice(LatticeId, latticeName, LatticeMinSupport); 
		
		Document document = null;
	    DocumentBuilderFactory factory = null;
	      
    	// creation d'une fabrique de documents
    	factory = DocumentBuilderFactory.newInstance();
    	
    	try {
    		// creation d'un constructeur de documents
    	    DocumentBuilder builder = factory.newDocumentBuilder();
    	    // lecture du contenu d'un fichier XML avec DOM
        	document = builder.parse(new File(LatticeXMLFileName));
    	}
    	catch (final ParserConfigurationException e) {
    	    e.printStackTrace();
    	}
    	catch (final SAXException e) {
    	    e.printStackTrace();
    	}
    	catch (final IOException e) {
    	    e.printStackTrace();
    	}
    	
    	// Traitement du document
    	Element racine = document.getDocumentElement();
    	
    	/* Recuperation des informations sur le treillis */
    	NodeList nbInnerConceptsNode = racine.getElementsByTagName("nb_inner_nodes"); 
    	if(nbInnerConceptsNode.item(0).getNodeType() == Node.ELEMENT_NODE) {
    		final Element nbInnerConceptsElement = (Element) nbInnerConceptsNode.item(0);
    		lat.nbInnerConcept = Integer.parseInt(nbInnerConceptsElement.getTextContent());
    	}
    	
    	NodeList topNode = racine.getElementsByTagName("top"); 
    	if(topNode.item(0).getNodeType() == Node.ELEMENT_NODE) {
    		final Element topElement = (Element) topNode.item(0);
    		lat.topConceptId = Integer.parseInt(topElement.getAttribute("concept_ref"));
    		lat.topConceptLevel = Integer.parseInt(topElement.getAttribute("level"));
    	}
    	
    	NodeList bottomNode = racine.getElementsByTagName("bottom"); 
    	if(bottomNode.item(0).getNodeType() == Node.ELEMENT_NODE) {
    		final Element bottomElement = (Element) bottomNode.item(0);
    		lat.bottomConceptId = Integer.parseInt(bottomElement.getAttribute("concept_ref"));
    		lat.bottomConceptLevel = Integer.parseInt(bottomElement.getAttribute("level"));
    	}
    	
    	/* Recuperation des objets */
    	NodeList objNodeList = racine.getElementsByTagName("obj");
    	//System.out.println( "Nombre d'obj : " + objNodeList.getLength());
    	for (int i = 0; i<objNodeList.getLength(); i++) {
            if(objNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
            	final Element objElement = (Element) objNodeList.item(i);
            	int objId = Integer.parseInt(objElement.getAttribute("id")) + idOffset;
            	String objName = objElement.getTextContent();
            	Lat_Object object = new Lat_Object(objId, objName);
            	lat.objects.put(objId, object);
            	//lat.objects.get(objId).toStringObject();
            }				
        }
    	
    	/* Recuperation des attributs */
    	NodeList attrNodeList = racine.getElementsByTagName("attr");
    	//System.out.println( "Nombre d'attr : " + attrNodeList.getLength());
        for (int i = 0; i<attrNodeList.getLength(); i++) {
            if(attrNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
            	final Element attrElement = (Element) attrNodeList.item(i);
            	int attrId = Integer.parseInt(attrElement.getAttribute("id"));
            	String attrText = attrElement.getTextContent();
            	Lat_Attribute attribute = new Lat_Attribute(attrId);
            	attribute.setAttributeFieldType(attrText);
            	attribute.setAttributeValue(attrText);
            	lat.attributes.put(attrId, attribute);
            	//lat.attributes.get(attrId).toStringAttribute();
            }				
        }
    	
        /* Recuperation des concepts du lattice */
        NodeList conceptNodeList = racine.getElementsByTagName("concept");
        //System.out.println( "Nombre de concepts du lattice : " + conceptNodeList.getLength());
        for (int i = 0; i<conceptNodeList.getLength(); i++) {
        	final Element conceptElement = (Element) conceptNodeList.item(i);
        	// Concept initialization
        	int conceptId = Integer.parseInt(conceptElement.getAttribute("id"));
        	int conceptSupport = Integer.parseInt(conceptElement.getAttribute("supp_abs"));
        	int conceptLevel = Integer.parseInt(conceptElement.getAttribute("level"));
        	String conceptType = conceptElement.getAttribute("type");
        	Lat_Concept concept = new Lat_Concept(conceptId, conceptSupport, conceptLevel);
        	concept.setConceptType(conceptType);
        	
        	// Set extent
        	NodeList extentNode = conceptElement.getElementsByTagName("extent");
        	if(extentNode.item(0).getNodeType() == Node.ELEMENT_NODE) {
        		final Element extentElement = (Element) extentNode.item(0);
        		int extentSize = Integer.parseInt(extentElement.getAttribute("size"));
        		if (extentSize > 1){
        			String[] objectList = extentElement.getTextContent().split(",");
        			for (int j=0; j<extentSize; j++){
        				int objectId = Integer.parseInt(objectList[j]) + idOffset;
        				concept.extent.add(lat.objects.get(objectId));
        			}
        		} else if (extentSize == 1){
        				int objectId = Integer.parseInt(extentElement.getTextContent()) + idOffset;
        				concept.extent.add(lat.objects.get(objectId));
        		}
        	}
        	
        	// Set intent
        	NodeList intentNode = conceptElement.getElementsByTagName("intent");
        	if(intentNode.item(0).getNodeType() == Node.ELEMENT_NODE) {
        		final Element intentElement = (Element) intentNode.item(0);
        		int intentSize = Integer.parseInt(intentElement.getAttribute("size"));
        		if (intentSize > 1){
        			String[] attributeList = intentElement.getTextContent().split(",");
        			for (int j=0; j<intentSize; j++){
        				int attributeId = Integer.parseInt(attributeList[j]);
        				concept.intent.add(lat.attributes.get(attributeId));
        			}
        		} else if (intentSize == 1){
    				int attributeId = Integer.parseInt(intentElement.getTextContent());
    				concept.intent.add(lat.attributes.get(attributeId));
        		}
        	}
        	
        	// Set parents
        	NodeList parentsNode = conceptElement.getElementsByTagName("parents");
        	if(parentsNode.item(0).getNodeType() == Node.ELEMENT_NODE) {
        		final Element parentsElement = (Element) parentsNode.item(0);
        		int parentsSize = Integer.parseInt(parentsElement.getAttribute("size"));
        		if (parentsSize > 1){
        			String[] parentsList = parentsElement.getTextContent().split(",");
        			for (int j=0; j<parentsSize; j++){
        				int parentsId = Integer.parseInt(parentsList[j]);
        				concept.parents.add(parentsId);
        			}
        		} else if (parentsSize == 1){
    				int parentsId = Integer.parseInt(parentsElement.getTextContent());
    				concept.parents.add(parentsId);
        		}
        	}
        	
        	// Set Children
        	NodeList childrenNode = conceptElement.getElementsByTagName("children");
        	if(childrenNode.item(0).getNodeType() == Node.ELEMENT_NODE) {
        		final Element childrenElement = (Element) childrenNode.item(0);
        		int childrenSize = Integer.parseInt(childrenElement.getAttribute("size"));
        		if (childrenSize > 1){
        			String[] childrenList = childrenElement.getTextContent().split(",");
        			for (int j=0; j<childrenSize; j++){
        				int childrenId = Integer.parseInt(childrenList[j]);
        				concept.children.add(childrenId);
        			}
        		} else if (childrenSize == 1){
    				int childrenId = Integer.parseInt(childrenElement.getTextContent());
    				concept.children.add(childrenId);
        		}
        	}
        	
        	// Set Generators
        	NodeList genNode = conceptElement.getElementsByTagName("gen");
        	if ( genNode.getLength() != 0 ){
	        	if(genNode.item(0).getNodeType() == Node.ELEMENT_NODE) {
	        		final Element genElement = (Element) genNode.item(0);
	        		int genSize = Integer.parseInt(genElement.getAttribute("size"));
	        		if (genSize > 1){
	        			String[] genList = genElement.getTextContent().split(",");
	        			for (int j=0; j<genSize; j++){
	        				int genId = Integer.parseInt(genList[j]);
	        				concept.generators.add(lat.attributes.get(genId));
	        			}
	        		} else if (genSize == 1){
	    				int genId = Integer.parseInt(genElement.getTextContent());
	    				concept.generators.add(lat.attributes.get(genId));
	        		}
	        	}
        	}
        	
        	lat.concepts.put(conceptId, concept);
        	
        }
        
    	return lat;
		
	}
	
	public ArrayList<Lat_Concept> getSignificantConcept (Lattice lattice){
		
		ArrayList<Lat_Concept> conceptCache = new ArrayList<Lat_Concept>();
		
		ArrayList<Object> objectStack = new ArrayList<Object>();
		ArrayList<Lat_Concept> conceptStack = new ArrayList<Lat_Concept>();
		
		Map<Lat_Object, ArrayList<Lat_Concept>> baseConceptList = new HashMap<Lat_Object, ArrayList<Lat_Concept>>() ;
			
		// Recuperation des concepts du niveau N-1
		Lat_Concept baseConcept = lattice.concepts.get(lattice.bottomConceptId);
		for (int i=0; i<baseConcept.parents.size(); i++){
			conceptCache.add( lattice.concepts.get(baseConcept.parents.get(i)) );
		}
		
		// Sort Concept per Object
		for (Map.Entry<Integer,Lat_Object> o : lattice.objects.entrySet()){
			
			Lat_Object object = o.getValue();
			ArrayList<Lat_Concept> conceptMatchList = new ArrayList<Lat_Concept>();
			
			for (int i=0; i<conceptCache.size(); i++){
				ArrayList<Lat_Object> conceptExtentList = conceptCache.get(i).extent;
				if (conceptExtentList.contains(object)){
					conceptMatchList.add(conceptCache.get(i));
				}
			}
			
			if ( !conceptMatchList.isEmpty() ) {
				baseConceptList.put(object, conceptMatchList);
			} else {
				System.err.println("ERROR: There is no concept match for rule "+ object.name + " (" + object.id + ")" );
				System.exit(-1);
			}
			
		}
		
		
		/*for (Entry<Lat_Object, ArrayList<Lat_Concept>> bclist : baseConceptList.entrySet()){
			bclist.getKey().toStringObject();
			for (int i=0; i< bclist.getValue().size(); i++){
				System.out.print( "\t" );
				bclist.getValue().get(i).toStringConcept();
			}
		}*/
		
		// Recuperation des concepts pour la construction du Trie
		for (Entry<Lat_Object, ArrayList<Lat_Concept>> bclist : baseConceptList.entrySet()){
			Lat_Object object = bclist.getKey();
			if (!objectStack.contains(object)){

				int nbConcept = bclist.getValue().size();
				if ( nbConcept < 2 ){
					Lat_Concept concept = bclist.getValue().get(0);
					if (!conceptStack.contains(concept))
						conceptStack.add(concept);
					for (int i=0; i<concept.extent.size(); i++){
						if (!objectStack.contains(concept.extent.get(i))){
							objectStack.add(concept.extent.get(i));
						}
					}
				} else {
					
					// Calculer le nombre de regles manquantes
					Map<Lat_Concept, Integer> nbRequiredObjectList = new HashMap<Lat_Concept, Integer>() ;
					for (int i=0; i<bclist.getValue().size(); i++){
						Lat_Concept thisConcept = bclist.getValue().get(i);
						int nbRequiredObject = 0;
						for (int j=0; j<thisConcept.extent.size(); j++){
							if (!objectStack.contains(thisConcept.extent.get(j))){
								nbRequiredObject ++;
							}
						} 
						nbRequiredObjectList.put(thisConcept, nbRequiredObject);
					}
					// Trouver le concept favoris en fonction du nombre d'objets requis
					int max = -1;
					Lat_Concept favouriteConcept = new Lat_Concept(-1,-1,-1) ;
					for (Entry<Lat_Concept, Integer> rolist : nbRequiredObjectList.entrySet()){
						if (!conceptStack.contains(rolist.getKey())){
							if (rolist.getValue() > max){
								max = rolist.getValue();
								favouriteConcept = rolist.getKey();
							}
						}
					}
					
					conceptStack.add(favouriteConcept);
					for (int i=0; i<favouriteConcept.extent.size(); i++){
						if (!objectStack.contains(favouriteConcept.extent.get(i)))
							objectStack.add(favouriteConcept.extent.get(i));	
					}
				
			}
		
		}
	}
		return conceptStack;
		
	}
	
			
			
			
	public ArrayList<Lat_Concept> getSignificantConcept2 (Lattice lattice, int taille){
		
		ArrayList<Lat_Concept> conceptCache = new ArrayList<Lat_Concept>();
		
		ArrayList<Object> objectStack = new ArrayList<Object>();
		ArrayList<Lat_Concept> conceptStack = new ArrayList<Lat_Concept>();
		
		Map<Lat_Object, ArrayList<Lat_Concept>> baseConceptList = new HashMap<Lat_Object, ArrayList<Lat_Concept>>() ;
			
		// Recuperation des concepts du niveau N-1
		Lat_Concept baseConcept = lattice.concepts.get(lattice.bottomConceptId);
		for (int i=0; i<baseConcept.parents.size(); i++){
			conceptCache.add( lattice.concepts.get(baseConcept.parents.get(i)) );
		}
		
		// Sort Concept per Object
		for (Map.Entry<Integer,Lat_Object> o : lattice.objects.entrySet()){
			
			Lat_Object object = o.getValue();
			ArrayList<Lat_Concept> conceptMatchList = new ArrayList<Lat_Concept>();
			
			for (int i=0; i<conceptCache.size(); i++){
				ArrayList<Lat_Object> conceptExtentList = conceptCache.get(i).extent;
				if (conceptExtentList.contains(object)){
					conceptMatchList.add(conceptCache.get(i));
				}
			}
			
			if ( !conceptMatchList.isEmpty() ) {
				baseConceptList.put(object, conceptMatchList);
			} else {
				System.err.println("ERROR: There is no concept match for rule "+ object.name + " (" + object.id + ")" );
				System.exit(-1);
			}
			
		}
		
		for (Entry<Lat_Object, ArrayList<Lat_Concept>> bclist : baseConceptList.entrySet()){
			for (int i=0; i<bclist.getValue().size();i++) {
				if (bclist.getValue().get(i).intent.size()<=taille) {
					if (!conceptStack.contains(bclist.getValue().get(i)))
					conceptStack.add(bclist.getValue().get(i));
				}
			}
		}

		return conceptStack;
	}

}

