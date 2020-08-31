private int[] dijkstra(double[][] pixelEnergy) {
  int h = pixelEnergy.length;
  int w = pixelEnergy[0].length;
  int source = h*w;
  int sink = h*w+1;


  int[] from = new int[h*w+2];
  int[] distTo = new int[h*w+2];
  IndexMinPQ<Double> pq = new IndexMinPQ<Double>(h*w+2);

  for (int p = 0; p<h*w+2: p++) {
    distTo[p] = Double.POSITIVE_INFINITY;
  }
  distTo[source] = 0.0;

  pq.insert(source, 0.0);
  while (!pq.isEmpty()) {
    int curr = pq.delMin();
    for (int neighbor: getNeighbors(curr, h, w)) {
      if (distTo[neighbor] > distTo[curr] + pixelEnergy[curr/w][curr%w]) {
        distTo[neighbor] = distTo[curr] + pixelEnergy[curr/w][curr%w];
        from[neighbor] = curr;
        if (pq.contains(neighbor)) {
          pq.decreaseKey(neighbor, distTo[neighbor])
        } else {
          pq.insert(neighbor, distTo[neighbor])
        }
      }
    }
  }
  int[] seam = getPath(nextPixel, h, w);
}
private int[] getPath(int[] spanTree, int h, int w) {
  int[] shortPath = new int[h];
  int curr = h*w+1

  int r = h;
  while(curr != h*w) {
    shortPath[--r] = spanTree[curr] % w;
    curr = spanTree[curr];
  }
  return shortPath;
}

private Array<Integer> getNeighbors(int pos, int h, int w) {

  Array<Integer> neighbors = new Array<>();
  if (pos == h*w) { // add the whole first row if at source point
    for (int x=0; x<w; x++) {
      neighbors.add(flatten(0,x);)
    }
    return neighbors;
  } else if (pos/w == h ){ // return the sink point if on the last row of the image
    neighbors.add(h*w+1);
    return neighbors;
  } else {
    int r = pos / w;
    int c = pos % w;

    if (c > 0) {
      neighbors.add(flatten(r+1,c-1,w));
    }
    neighbors.add(flatten(r+1,c,w));
    if (c < w-1) {
      neighbors.add(flatten(r+1,c+1,w));
    }
    return neighbors;
  }
}

private int flatten(int r, int c, int w) {
  return r*w+c;
}
