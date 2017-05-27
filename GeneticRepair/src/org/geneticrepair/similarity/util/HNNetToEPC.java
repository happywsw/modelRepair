package org.geneticrepair.similarity.util;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jbpt.pm.epc.Epc;
import org.processmining.framework.log.LogEvent;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.models.ModelGraphEdge;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.epcpack.ConfigurableEPC;
import org.processmining.framework.models.epcpack.EPCConnector;
import org.processmining.framework.models.epcpack.EPCEdge;
import org.processmining.framework.models.epcpack.EPCEvent;
import org.processmining.framework.models.epcpack.EPCFunction;
import org.processmining.framework.models.epcpack.EPCObject;
import org.processmining.framework.models.epcpack.algorithms.ConnectorStructureExtractor;
import org.processmining.framework.models.heuristics.HNSet;
import org.processmining.framework.models.heuristics.HNSubSet;
import org.processmining.framework.models.heuristics.HeuristicsNet;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.mining.MiningResult;
import org.processmining.mining.epcmining.EPCResult;

import nl.tue.tm.is.epc.Arc;
import nl.tue.tm.is.epc.Connector;
import nl.tue.tm.is.epc.EPC;
import nl.tue.tm.is.epc.Event;
import nl.tue.tm.is.epc.Function;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Boudewijn van Dongen
 * @version 1.0
 */

//����ʽ��-> EPC
public class HNNetToEPC{
	private static final String EVENTSTRING = "Status change to \\n";

	public HNNetToEPC() {
	}

	//modify  net -> EPC
		public EPC convert(HeuristicsNet original) {
			HeuristicsNet hNet = null;
			LogReader log = null;
			EPC tmEpc = new EPC();
			hNet = original;
			if (hNet == null) {
				return null;
			}

			ConfigurableEPC epc = new org.processmining.framework.models.epcpack.EPC();
			
			EPCFunction[] functions = new EPCFunction[hNet.getDuplicatesMapping().length];
			EPCEvent[] events = new EPCEvent[hNet.getDuplicatesMapping().length];

			//creating and connecting the events to functions
			for (int index = 0; index < hNet.getDuplicatesMapping().length; index++) {

				LogEvent le = hNet.getLogEvents().getEvent(hNet.
							  getDuplicatesMapping()[index]);
				// Skip those tasks that do not have arcs and are not the initial or final task
				functions[index] = new EPCFunction(le, epc);
				events[index] = new EPCEvent(EVENTSTRING + le.getModelElementName() + " " +
								le.getEventType(), epc);
				epc.addEdge(epc.addEvent(events[index]),
						epc.addFunction(functions[index]));
			}
			
			HashMap mapping = new HashMap();

			EPCEvent initialEvent = new EPCEvent("fictive start", epc);
			epc.addEvent(initialEvent);

			EPCConnector initialConn = new EPCConnector(EPCConnector.XOR, epc);
			epc.addConnector(initialConn);

			epc.addEdge(initialEvent, initialConn);
			
			for (int i = 0; i < hNet.getInputSets().length; i++) {
				// Look at the AND-set
				HNSet andSet = hNet.getInputSets()[i];

				EPCObject andConn = events[i];
				if (andConn == null) {
					continue;
				}

				if (andSet.size() == 0) {
					epc.delEvent((EPCEvent) andConn);
					epc.addEdge(initialConn, functions[i]);
					andConn = initialConn;
				}

				if (andSet.size() > 1) {
					EPCConnector c = epc.addConnector(new EPCConnector(EPCConnector.AND, epc));
					epc.addEdge(c, andConn);
					andConn = c;
				}

				for (int orSetIt = 0; orSetIt < andSet.size(); orSetIt++) {
					HNSubSet orSet = andSet.get(orSetIt);
					EPCObject xorConn = andConn;
					if (orSet.size() > 1) {
						xorConn = epc.addConnector(new EPCConnector(EPCConnector.XOR, epc));
						epc.addEdge(xorConn, andConn);
					}
					mapping.put(new UniqueSet(orSet, i, true), xorConn);
				}
			}
			if (initialConn.outDegree() == 1) {
				epc.addEdge(initialEvent, (EPCObject) initialConn.getSuccessors().iterator().next());
				epc.delConnector(initialConn);
			}

			EPCEvent finalEvent = new EPCEvent("fictive end", epc);
			epc.addEvent(finalEvent);

			EPCConnector finalConn = new EPCConnector(EPCConnector.XOR, epc);
			epc.addConnector(finalConn);

			epc.addEdge(finalConn, finalEvent);

			for (int i = 0; i < hNet.getOutputSets().length; i++) {
				// Look at the AND-set
				HNSet andSet = hNet.getOutputSets()[i];

				EPCObject andConn = functions[i];
				if (andConn == null) {
					continue;
				}

				if (andSet.size() == 0) {
					epc.addEdge(andConn, finalConn);
				}

				if (andSet.size() > 1) {
					EPCConnector c = epc.addConnector(new EPCConnector(EPCConnector.AND, epc));
					epc.addEdge(andConn, c);
					andConn = c;
				}
				
				
				for (int orSetIt = 0; orSetIt < andSet.size(); orSetIt++) {
					HNSubSet orSet = andSet.get(orSetIt);
					EPCObject xorConn = andConn;
					if (orSet.size() > 1) {
						xorConn = epc.addConnector(new EPCConnector(EPCConnector.XOR, epc));
						epc.addEdge(andConn, xorConn);
					}
					mapping.put(new UniqueSet(orSet, i, false), xorConn);
				}
			}

			if (finalConn.inDegree() == 1) {
				epc.addEdge((EPCObject) finalConn.getPredecessors().iterator().next(), finalEvent);
				epc.delConnector(finalConn);
			}

			// Every Function is now an atomic connected part of the graph. It has
			// a number of input connectors and output connectors, each of which
			// correspond to a specific inSet or outSet

			// Now, look at all the edges that have to be made.
			for (int i = 0; i < hNet.getOutputSets().length; i++) {
				HNSet andSet = hNet.getOutputSets()[i];

				for (int j = 0; j < andSet.size(); j++) {

					for (int orSetIt = 0; orSetIt < andSet.size(); orSetIt++) {
						HNSubSet orSet = andSet.get(orSetIt);
						for (int destIt = 0; destIt < orSet.size(); destIt++) {
							int k = orSet.get(destIt);
							// There is a connection between epc.getFunctions.get(i) -->
							//                               epc.getFunctions.get(k);
							// This edge corresponds to orSet --> some orSet in the InSet collection of k

							HNSubSet t = null;
							HNSet inSet = hNet.getInputSet(k);
							for (int inSetIt = 0; inSetIt < inSet.size(); inSetIt++) {
								if ((t == null)) {
									HNSubSet t2 = inSet.get(inSetIt);
									if (t2.contains(i)) {
										t = t2;
									}
								} else {
									break;
								}
							}

							if (t != null) {
								UniqueSet s1 = null;
								UniqueSet s2 = new UniqueSet(orSet, i, false);
								Iterator keys = mapping.keySet().iterator();
								do {
									s1 = (UniqueSet) keys.next();
								} while (!s1.equals(s2));
								EPCObject o1 = (EPCObject) mapping.get(s1);

								s1 = null;
								s2 = new UniqueSet(t, k, true);
								keys = mapping.keySet().iterator();
								do {
									s1 = (UniqueSet) keys.next();
								} while (!s1.equals(s2));
								EPCObject o2 = (EPCObject) mapping.get(s1);

								epc.addEdge(o1, o2);
							}
						}
					}
				}
			}
			for (int i = hNet.getInputSets().length - 1; i > 0; i--) {
				if ((hNet.getInputSets()[i].size() == 0 && !hNet.getStartTasks().contains(i)) &&
					(hNet.getOutputSets()[i].size() == 0 && !hNet.getEndTasks().contains(i)))  {
					// remove this event and function
					epc.delFunction(functions[i]);
					epc.delEvent(events[i]);
				}

			}
			epc=ConnectorStructureExtractor.extract(epc,true,false,false,false,false,false,false,true,false,false);
			if(epc.isValidEPC() != ""){
				return null;
			}else{
				tmEpc = convertToEPC(epc);
				return tmEpc;
			}
		}
		
	public EPC convertToEPC(ConfigurableEPC conEPC) {
		EPC epc = new EPC();

		List<EPCFunction> founctions = conEPC.getFunctions();
		List<EPCEvent> events = conEPC.getEvents();
		List<EPCConnector> connectors = conEPC.getConnectors();
		List<ModelGraphEdge> arcs = conEPC.getRealEdges();

		// load founctions
		for (EPCFunction e : founctions) {
			Function f = new Function();
			f.setId(e.getId() + "");
			f.setName(e.getIdentifier());
			epc.addFunction(f);
		}

		// load events
		for (EPCEvent p : events) {
			Event ev = new Event();
			ev.setId(p.getId() + "");
			ev.setName(p.getIdentifier());
			epc.addEvent(ev);
		}

		// load connector
		for (EPCConnector c : connectors) {
			Connector connector = new Connector();
			connector.setId(c.getId() + "");
			connector.setName(c.getTypeFull());
			epc.addConnector(connector);
		}

		// load arc
		for (ModelGraphEdge m : arcs) {
			Arc arc = new Arc();
			arc.setId(m.getId() + "");

			ModelGraphVertex vertex = m.getSource();
			ModelGraphVertex dest = m.getDest();

			arc.setSource(epc.findNode(vertex.getId() + ""));
			arc.setTarget(epc.findNode(dest.getId() + ""));
			epc.addArc(arc);
		}
		epc.cleanEPC();
		return epc;
	}
		
		
	public boolean accepts(ProvidedObject original) {
		int i = 0;
		boolean hasNet = false;
		while (!(hasNet) && (i < original.getObjects().length)) {
			hasNet = hasNet || (original.getObjects()[i] instanceof HeuristicsNet);
			i++;
		}
		return (hasNet);
	}

	public String getName() {
		return "Heuristic net to EPC";
	}

	public String getHtmlDescription() {
		return "This plug-in converts a HeusticsNet to an Event-Driven Process Chains (EPC).";
	}

}

class UniqueSet {
	public HNSubSet set;
	public int id;
	public boolean in;

	public UniqueSet(HNSubSet set, int id, boolean in) {
		this.set = set;
		this.id = id;
		this.in = in;
	}

	public boolean equals(Object o) {
		if (!(o instanceof UniqueSet)) {
			return false;
		}

		UniqueSet s = (UniqueSet) o;
		return (s.set.equals(set)) && (s.id == id) && (s.in == in);

	}

	public String toString() {
		return set.toString();
	}

}
