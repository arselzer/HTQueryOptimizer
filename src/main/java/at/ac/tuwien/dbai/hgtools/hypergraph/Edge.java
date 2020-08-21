package at.ac.tuwien.dbai.hgtools.hypergraph;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dbai.hgtools.util.Util;

public class Edge {
	private String name;
	/** Vertices are ordered in every edge. */
	private ArrayList<String> vertices;

	public Edge(String name) {
		this.name = name;
		vertices = new ArrayList<String>(200);
	}

	public Edge(String name, String[] strings) {
		this.name = name;
		vertices = new ArrayList<String>(strings.length);
		for (String s : strings) {
			vertices.add(s);
		}
	}

	public String getName() {
		return name;
	}

	public boolean contains(String v) {
		return vertices.contains(v);
	}

	public boolean addVertex(String v) {
		if (vertices.contains(v)) {
			return false;
		}
		vertices.add(v);
		return true;
	}

	public boolean renameVertex(String v, String newName) {
		if (vertices.contains(newName)) {
			return false;
		}
		int pos = vertices.indexOf(v);
		vertices.set(pos, newName);
		return true;
	}

	public List<String> getVertices() {
		return vertices;
	}

	/*
	 * @Override public boolean equals(Object obj) { if (obj == this) { return true;
	 * } if (!(obj instanceof Edge)) { return false; } Edge oth = (Edge) obj; if
	 * (this.vertices.size() != oth.vertices.size()) { return false; }
	 * HashSet<String> myEdges = new HashSet<>(vertices); HashSet<String> othEdges =
	 * new HashSet<>(); for (String v : myEdges) { if (!othEdges.contains(v)) {
	 * return false; } } return true; }
	 * 
	 * @Override public int hashCode() { return new HashSet<>(vertices).hashCode();
	 * }
	 */
	
	public String toString() {
		String s = "";

		s += Util.stringify(name) + "(";
		for (String v : vertices) {
			s += Util.stringify(v) + ",";
		}
		s = s.substring(0, s.length() - 1);
		s += ")";

		return s;
	}

}
