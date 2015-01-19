package wmh;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class Ant
{
	LinkedList<Edge> path;
	Vertex currentVertex;
	Vertex previousVertex;
	int startIdx = -1;

	@Override
	public String toString()
	{
		int lastIdx = startIdx;
		String res = "current: " + currentVertex.idx + ", path: " + startIdx;
		for (Edge e : path)
		{
			int newIdx = (e.begin.idx == lastIdx ? e.end.idx : e.begin.idx);
			res += " " + newIdx;
			lastIdx = newIdx;
		}
		return res;
	}

	public Ant()
	{
		path = new LinkedList<Edge>();
	}

	public void takeStep()
	{
		double sum = 0.0;
		int size = currentVertex.edges.size();
		double[] distr = new double[size];
		int i = 0;

		for (Edge e : currentVertex.edges)
		{
			if (previousVertex.idx == e.begin.idx
					|| previousVertex.idx == e.end.idx)
			{
				distr[i] = currentVertex.edges.size() == 1 ? Double.MAX_VALUE
						: Double.MIN_VALUE;
				++i;
				continue;
			}
			sum += e.effectiveAttractiveness;
			distr[i] = sum;
			++i;
		}

		double p = (new Random().nextDouble()) * sum;

		for (i = 0; i < size; ++i)
		{
			if (p < distr[i])
			{
				Edge e = currentVertex.edges.get(i);
				path.add(e);
				previousVertex = currentVertex;
				currentVertex = e.begin.idx == currentVertex.idx ? e.end
						: e.begin;
				break;
			}
		}
	}

	public void removeLoops()
	{
		LinkedList<Integer> vertices = new LinkedList<Integer>();
		LinkedList<Integer> finalVertices = new LinkedList<Integer>();
		vertices.add(startIdx);
		int lastVertex = startIdx;
		int nextVertex;

		for (Edge e : path)
		{
			nextVertex = e.begin.idx == lastVertex ? e.end.idx : e.begin.idx;
			vertices.add(nextVertex);
			lastVertex = nextVertex;
		}
		HashSet<Integer> visitedVertices = new HashSet<Integer>();

		for (int i : vertices)
		{
			if (!visitedVertices.contains(i))
			{
				finalVertices.add(i);
				visitedVertices.add(i);
			} else
			{
				do
				{
					visitedVertices.remove(finalVertices.removeLast());
				} while (finalVertices.peekLast() != i);
			}
		}

		if (vertices.size() == visitedVertices.size())
			return;

		LinkedList<Edge> finalPath = new LinkedList<Edge>();

		ListIterator<Integer> iter = finalVertices.listIterator();
		int currentBegin = iter.next();
		int currentEnd = iter.next();
		for (Edge e : path)
		{
			if ((e.begin.idx == currentBegin && e.end.idx == currentEnd)
					|| (e.end.idx == currentBegin && e.begin.idx == currentEnd))
			{
				finalPath.add(e);
				currentBegin = currentEnd;
				if (iter.hasNext())
					currentEnd = iter.next();
			}
		}
	}

	public double getPathCost()
	{
		double sum = 0.0;
		for (Edge e : path)
		{
			sum += e.weight;
		}
		return sum;
	}
}
