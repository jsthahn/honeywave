package org.anyframe.cargotracker.infrastructure.routing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransitPath implements Serializable {

	private static final long serialVersionUID = -2467364817762586824L;
	
	private List<TransitEdge> transitEdges;

    public TransitPath() {
        this.transitEdges = new ArrayList<>();
    }

    public TransitPath(List<TransitEdge> transitEdges) {
        this.transitEdges = transitEdges;
    }

    public List<TransitEdge> getTransitEdges() {
        return transitEdges;
    }

    public void setTransitEdges(List<TransitEdge> transitEdges) {
        this.transitEdges = transitEdges;
    }

    @Override
    public String toString() {
        return "TransitPath{" + "transitEdges=" + transitEdges + '}';
    }
}
