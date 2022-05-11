import java.util.*;
import java.util.function.Consumer;

public class BinarySearchTree<T extends Comparable<T>> implements Iterable<T>{
    private ArrayList<T> values;
    private ArrayList<T> originalValues;
    private String name;



    Node<T> root;
    public BinarySearchTree(String name){
        this.name = name;
        values = new ArrayList<T>();
        originalValues = new ArrayList<T>();
        root = null;

    }

    public void addAll(Collection<? extends T> input){
        values.addAll(input);
        Collections.sort(values);
        originalValues.addAll(input);
        makeBST(originalValues);
    }

    public ArrayList<T> getValues(){
        return this.values;
    }


    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < values.size();
            }

            @Override
            public T next() {
                if(currentIndex == 0) Collections.sort(values);
                if(this.hasNext()) {
                    T currentElement = values.get(currentIndex);
                    currentIndex++;
                    return currentElement;
                }
                else return null;
            }

//            public void forEach(Consumer<? super T> action){
//                for (Iterator<T> it = this; it.hasNext(); ) {
//                    T t = it.next();
//                    action.accept(t);
//                }
//            }
        };
    }

    public void makeBST(List<T> values){
        if(this.root == null){
            this.root = new Node<T>(values.get(0));
        }

        for(int i=1;i<values.size();i++){
            insertIntoBST(this.root,values.get(i));
        }

    }

    public void insertIntoBST(Node<T> root,T value){
        if(root.data.compareTo(value) > 0){
            if(root.left != null){
                insertIntoBST(root.left,value);
                return;
            } else{
                Node<T> newNode = new Node<T>(value);
                root.left = newNode;
                return;
            }
        } else{
            if(root.right != null){
                insertIntoBST(root.right,value);
                return;
            } else{
                Node<T> newNode = new Node<T>(value);
                root.right = newNode;
                return;
            }
        }
    }

    //make bst starting from original values and iterating through, inserting into bst,
    //So first entry is root, then insert based on less than or equal to current node until null is found.

    public String toString(){
        return "[" + this.name + "] " + preorder(this.root);
    }

    public String preorder(Node<T> root){
        if(root == null){
            return "";
        }

        String result = "";

        result += String.valueOf(root.data);

        if(root.left != null) {
            result += " L:(" + preorder(root.left) + ")";
        }
        if(root.right != null) {
            result += " R:(" + preorder(root.right) + ")";
        }
        return result;
    }




    public static <T extends Comparable<T>> List<T> merge(List<BinarySearchTree<T>> bstlist){
        auxList<T> aux = new auxList<T>(bstlist.size());
        ArrayList<Thread> workers = new ArrayList<>();
        int currID = 0;
        for(BinarySearchTree<T> bst : bstlist){

            List<T> lst = bst.values;

            Worker<T> worker = new Worker<T>(currID,aux,lst);
            workers.add(worker);
            worker.start();
            currID++;
        }

        Master<T> master = new Master<>(aux,workers);
        master.start();
        while(master.isAlive()){

        }

        return master.mergedList;

//        List<Integer> t1 = Arrays.asList(2,3,4,5);
//        List<Integer> t2 = Arrays.asList(1,3,5,6);
//        List<Integer> t3 = Arrays.asList(1,3,7,11);
//        List<Integer> t4 = Arrays.asList(1,5,6,7,8,9);
//        auxList<Integer> aux = new auxList<>(4);
//        Worker<Integer> w1 = new Worker<Integer>(0,aux,t1);
//        Worker<Integer> w2 = new Worker<Integer>(1,aux,t2);
//        Worker<Integer> w3 = new Worker<Integer>(2,aux,t3);
//        Worker<Integer> w4 = new Worker<Integer>(3,aux,t4);
//        ArrayList<Thread> workers = new ArrayList<>();
//        workers.add(w1);
//        workers.add(w2);
//        workers.add(w3);
//        workers.add(w4);
//        w1.start();
//        w2.start();
//        w3.start();
//        w4.start();
//        Master<Integer> master = new Master<>(aux,workers);
//
//
//        master.start();
//
//        while(master.isAlive()){
//
//        }
//
//
//        System.out.println(master.mergedList.toString());
//
//
//
//
//
   }







    public static void main(String... args) {

        BinarySearchTree<Integer> t1 = new BinarySearchTree<>("Oak");
// adds the elements to t1 in the order 5, 3, 0, and then 9
        t1.addAll(Arrays.asList(5, 3, 0, 9));
        BinarySearchTree<Integer> t2 = new BinarySearchTree<>("Maple");
// adds the elements to t2 in the order 9, 5, and then 10
        t2.addAll(Arrays.asList(9, 5, 10));
        System.out.println(t1); // see the expected output for exact format
        t1.forEach(System.out::println); // iteration in increasing order
                System.out.println(t2); // see the expected output for exact format
        t2.forEach(System.out::println); // iteration in increasing order
        BinarySearchTree<String> t3 = new BinarySearchTree<>("Cornucopia");
        t3.addAll(Arrays.asList("coconut", "apple", "banana", "plum", "durian",
                "no durians on this tree!", "tamarind"));
        System.out.println(t3); // see the expected output for exact format
        t3.forEach(System.out::println); // iteration in increasing order




        List<BinarySearchTree<Integer>> bsts = new ArrayList<>();
        bsts.add(t1);
        bsts.add(t2);
        List<Integer> fin = BinarySearchTree.merge(bsts);

        System.out.println(fin.toString());



    }
}



class Node<T>{
    T data;

    Node<T> left;
    Node<T> right;

    Node(T data){
        this.data = data;
        this.left = null;
        this.right = null;
    }

}


class Master<T extends Comparable<T>> extends Thread{


    ArrayList<T> mergedList;
    auxList<T> reports;
    ArrayList<Thread> workers;
    public Master(auxList<T> reports,ArrayList<Thread> workers){
        this.mergedList = new ArrayList<T>();
        this.reports = reports;
        this.workers = workers;

    }


    public void run(){
        beginMerge();



        while(workersAlive() || valuesLeft()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            T minimum = reports.resource.get(0);
            int mindex = 0;
            int index = 0;
            int nulls = 0;
            for(T element : reports.resource){
                if(element == null) nulls++;
                if(minimum == null && element != null){
                    minimum = element;
                    mindex = index;
                } else {
                    if (element != null && element.compareTo(minimum) < 0) {
                        minimum = element;
                        mindex = index;
                    }
                }
                index++;
            }
            if (minimum != null) {
                mergedList.add(minimum);
                reports.resource.set(mindex, null);
            }




        }
    }

    public boolean workersAlive(){
        int alive = 0;
        for(Thread t : this.workers){
            if(t.isAlive()) alive++;
        }

        return alive > 0;
    }


    public void beginMerge(){
//        for(Thread t: this.workers){
//            t.start();
//        }
    }


    public boolean valuesLeft(){
        int num = 0;
        for(T element : reports.resource){
            if (element != null) num++;
        }
        if(num > 0) return true;
        return false;
    }

}


class Worker<T extends Comparable<T>> extends Thread {

    auxList<T> resource;
    int id;
    List<T> lst;
    int index;
    public Worker(int id,auxList<T> resource,List<T> lst ) {
        this.id = id;
        this.resource = resource;
        this.lst = lst;
        this.index = 0;
    }

    @Override
    public void run() {

        while(index < lst.size()){
            if(resource.resource.get(id) == null){
                T min = lst.get(index);
                index++;
                resource.resource.set(id,min);

            }
        }

    }



}


class auxList<T> {

    public ArrayList<T> resource;

    public auxList(int numThreads){
        resource = new ArrayList<T>(numThreads);
        for(int i=0;i<numThreads;i++){
            resource.add(null);
        }
    }

    public synchronized boolean addToList(int id,T element){
        if(resource.get(id) == null){
            resource.set(id,element);
            return true;
        } else{
            return false;
        }
    }




}

