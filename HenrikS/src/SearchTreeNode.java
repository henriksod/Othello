import java.util.HashSet;
import java.util.Set;

/**
 * SearchTreeNode is a part of what makes up a Search Tree. The root has to be kept track of since there is no
 * back propagation.
 * @author Henrik
 * 11/3/2017
 */
public class SearchTreeNode<T> {
    private T vertex;
    private Set<SearchTreeNode<T>> children;

    /**
     * SearchTreeNode constructor
     * @param vertex Node data.
     */
    public SearchTreeNode(T vertex) {
        this.vertex = vertex;
        children = new HashSet<SearchTreeNode<T>>();
    }

    /**
     * @return Node data
     */
    public T getVertex() {
        return vertex;
    }

    /**
     * @return Node children
     */
    public Set<SearchTreeNode<T>> getChildren() {
        return children;
    }

    /**
     * @param child New child
     */
    public void addChild(SearchTreeNode<T> child) {
        children.add(child);
    }

    /**
     * @return If node is a leaf or not
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

}
