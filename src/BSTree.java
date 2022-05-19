/**
 * @author back
 */
public class BSTree {
    public Node root;

    public Node find(long key){
        if (root == null){
            System.out.println("The tree is empty");
            return null;
        }

        Node current = root;

        while (current.userId != key) {
            if (current.userId > key) {
                current = current.leftChild;
            } else {
                current = current.rightChild;
            }
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    public boolean insert(Node node){
        if (root==null) {
            root = node;
            return true;
        }
        //TODO 不允许插入重复项

        Node current = root;
        while (current != null) {
            if (node.userId>current.userId) {
                if (current.rightChild == null) {
                    current.rightChild = node;
                    return true;
                }
                current = current.rightChild;
            }else {
                if (current.leftChild == null) {
                    current.leftChild = node;
                    return true;
                }
                current = current.leftChild;
            }
        }
        return false;
    }
}