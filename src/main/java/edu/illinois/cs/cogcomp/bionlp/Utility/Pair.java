package edu.illinois.cs.cogcomp.bionlp.Utility;


import java.io.Serializable;

public class Pair<S, T> implements Comparable, Serializable {
	private S first;

	private T second;

	public Pair(S first, T second) {
		this.first = first;
		this.second = second;
	}

	public S getFirst() {
		return first;
	}

	public void setFirst(S first) {
		this.first = first;
	}

	public T getSecond() {
		return second;
	}

	public void setSecond(T second) {
		this.second = second;
	}

	public String toString() {
		return "[" + getFirst() + " ; " + getSecond() + "]";
	}

	public boolean equals(Object o) {
		if (o instanceof Pair) {
			Pair p = (Pair) o;
			return p.getFirst().equals(getFirst()) && p.getSecond().equals(getSecond());
		}
		return false;
	}

	public int compareTo(Object o) {
		if (o instanceof Pair) {
			if (((Pair) o).getFirst() instanceof Comparable) {
				if (!((Comparable) getFirst()).equals(((Pair) o).getFirst()))
					return ((Comparable) getFirst()).compareTo(((Pair) o).getFirst());
				else if (((Pair) o).getSecond() instanceof Comparable && !((Comparable) getSecond()).equals(((Pair) o).getSecond()))
					return ((Comparable) getSecond()).compareTo(((Pair) o).getSecond());
				else
					return 0;
			} else
				throw new IllegalStateException("The type " + o.getClass() + " is not comparable!");
		} else {
			if (o.getClass().equals(getFirst().getClass())) {
				if (o instanceof Comparable) {
					return ((Comparable) o).compareTo(getFirst());
				} else
					throw new IllegalStateException("The type " + o.getClass() + " is not comparable!");
			}
			throw new IllegalStateException("The classes " + o.getClass() + " and " + getFirst().getClass() + " are not equal!");
		}
	}

	public Pair clone() {
		return new Pair<S, T>(getFirst(), getSecond());
	}

	@Override
	public int hashCode() {
		return getFirst().hashCode() + getSecond().hashCode();
	}

}