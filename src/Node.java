/**
 * @author back
 */
public class Node<T> {
    public long userId;
    public T thread;
    public Node leftChild;
    public Node rightChild;

    public Node(long id, T thread) {
        this.userId = id;
        this.thread = thread;
    }
}