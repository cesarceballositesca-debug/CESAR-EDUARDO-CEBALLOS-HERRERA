import java.awt.Color;

public class RBT<T extends Comparable<T>> {
    public Nodo<T> root;
    public int size;

    public RBT() {
        this.root = null;
        this.size = 0;
    }

    // ==========================
    // BÚSQUEDA INTERNA
    // ==========================
    private Nodo<T> searchNode(Nodo<T> nodo, T key) {
        while (nodo != null) {
            int cmp = key.compareTo(nodo.elemento);
            if (cmp == 0) return nodo;
            if (cmp < 0) nodo = nodo.left;
            else nodo = nodo.right;
        }
        return null;
    }

    // ==========================
    // INSERT (RBT)
    // ==========================
    public void insert(T elemento) {
        Nodo<T> z = new Nodo<>(elemento);
        size++;

        // inserción tipo BST
        Nodo<T> y = null;
        Nodo<T> x = root;

        while (x != null) {
            y = x;
            if (z.elemento.compareTo(x.elemento) < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        z.parent = y;
        if (y == null) {
            root = z; // árbol estaba vacío
        } else if (z.elemento.compareTo(y.elemento) < 0) {
            y.left = z;
        } else {
            y.right = z;
        }

        // arreglo de colores/rotaciones
        fixInsert(z);
    }

    private void fixInsert(Nodo<T> z) {
        while (z.parent != null && z.parent.color == Color.RED) {
            Nodo<T> abuelo = z.parent.parent;
            if (abuelo == null) break;

            // padre es hijo IZQUIERDO del abuelo
            if (z.parent == abuelo.left) {
                Nodo<T> tio = abuelo.right;

                // caso 1: tío rojo -> recoloreo
                if (tio != null && tio.color == Color.RED) {
                    z.parent.color = Color.BLACK;
                    tio.color = Color.BLACK;
                    abuelo.color = Color.RED;
                    z = abuelo;
                } else {
                    // caso 2/3: tío negro
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.color = Color.BLACK;
                    abuelo.color = Color.RED;
                    rightRotate(abuelo);
                }
            } else {
                // simétrico: padre es hijo DERECHO
                Nodo<T> tio = abuelo.left;

                if (tio != null && tio.color == Color.RED) {
                    z.parent.color = Color.BLACK;
                    tio.color = Color.BLACK;
                    abuelo.color = Color.RED;
                    z = abuelo;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = Color.BLACK;
                    abuelo.color = Color.RED;
                    leftRotate(abuelo);
                }
            }
        }
        root.color = Color.BLACK;
    }

    // ==========================
    // DELETE (RBT)
    // ==========================
    public void delete(T key) {
        Nodo<T> z = searchNode(root, key);
        if (z == null) return; // no está en el árbol, no hacemos nada
        deleteNode(z);
        size = Math.max(0, size - 1);
    }

    private void deleteNode(Nodo<T> z) {
        Nodo<T> y = z;
        Nodo<T> x;
        Nodo<T> parentX;
        Color yOriginalColor = y.color;

        if (z.left == null) {
            x = z.right;
            parentX = z.parent;
            transplant(z, z.right);
        } else if (z.right == null) {
            x = z.left;
            parentX = z.parent;
            transplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;
            parentX = y.parent;

            if (y.parent == z) {
                parentX = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }

            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }

        if (yOriginalColor == Color.BLACK) {
            fixDelete(x, parentX);
        }
    }

    private Nodo<T> minimum(Nodo<T> nodo) {
        while (nodo.left != null) {
            nodo = nodo.left;
        }
        return nodo;
    }

    // ==========================
    // FIX DELETE
    // ==========================
    private Color getColor(Nodo<T> n) {
        return (n == null) ? Color.BLACK : n.color;
    }

    private void fixDelete(Nodo<T> x, Nodo<T> parent) {
        while (x != root && getColor(x) == Color.BLACK) {
            if (x == (parent != null ? parent.left : null)) {
                Nodo<T> w = parent.right;

                if (getColor(w) == Color.RED) {
                    w.color = Color.BLACK;
                    parent.color = Color.RED;
                    leftRotate(parent);
                    w = parent.right;
                }

                if (getColor(w.left) == Color.BLACK && getColor(w.right) == Color.BLACK) {
                    if (w != null) w.color = Color.RED;
                    x = parent;
                    parent = x.parent;
                } else {
                    if (getColor(w.right) == Color.BLACK) {
                        if (w.left != null) w.left.color = Color.BLACK;
                        if (w != null) w.color = Color.RED;
                        rightRotate(w);
                        w = parent.right;
                    }
                    if (w != null) w.color = parent.color;
                    parent.color = Color.BLACK;
                    if (w.right != null) w.right.color = Color.BLACK;
                    leftRotate(parent);
                    x = root;
                }
            } else {
                Nodo<T> w = parent.left;

                if (getColor(w) == Color.RED) {
                    w.color = Color.BLACK;
                    parent.color = Color.RED;
                    rightRotate(parent);
                    w = parent.left;
                }

                if (getColor(w.right) == Color.BLACK && getColor(w.left) == Color.BLACK) {
                    if (w != null) w.color = Color.RED;
                    x = parent;
                    parent = x.parent;
                } else {
                    if (getColor(w.left) == Color.BLACK) {
                        if (w.right != null) w.right.color = Color.BLACK;
                        if (w != null) w.color = Color.RED;
                        leftRotate(w);
                        w = parent.left;
                    }
                    if (w != null) w.color = parent.color;
                    parent.color = Color.BLACK;
                    if (w.left != null) w.left.color = Color.BLACK;
                    rightRotate(parent);
                    x = root;
                }
            }
        }
        if (x != null) x.color = Color.BLACK;
    }

    // ==========================
    // ROTACIONES Y TRANSPLANT
    // ==========================
    private void leftRotate(Nodo<T> x) {
        Nodo<T> y = x.right;
        if (y == null) return;

        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }

        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Nodo<T> x) {
        Nodo<T> y = x.left;
        if (y == null) return;

        x.left = y.right;
        if (y.right != null) {
            y.right.parent = x;
        }

        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }

        y.right = x;
        x.parent = y;
    }

    private void transplant(Nodo<T> u, Nodo<T> v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) {
            v.parent = u.parent;
        }
    }

    // ==========================
    // INORDER (opcional para debug)
    // ==========================
    public void inOrder() {
        if (root == null) {
            System.out.println("Árbol vacío");
            return;
        }
        inOrderRec(root);
    }

    private void inOrderRec(Nodo<T> nodo) {
        if (nodo.left != null) inOrderRec(nodo.left);
        System.out.println(nodo);
        if (nodo.right != null) inOrderRec(nodo.right);
    }
}
