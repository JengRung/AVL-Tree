import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * 
 * TODO AVL Tree HW
 *
 * @author Jeng-Rung Tu. Created Sep 13, 2018.
 * @param <T>
 */

public class AVLTree<T extends Comparable<T>> {
	public BinaryNode root;
	private int rotationNum;

	public AVLTree() {
		root = null;
		this.rotationNum = 0;
	}

	public AVLTree(BinaryNode n) {
		root = n;
	}

	public boolean isEmpty() {
		if (this.root == null) {
			return true;
		}
		return false;
	}

	public int height() {

		if (isEmpty())
			return -1;

		return this.root.adjustHeight();
	}

	public int getRotationCount() {
		return this.rotationNum;
	}

	public int size() {

		if (isEmpty())
			return 0;

		return root.getSize();
	}

	public String toString() {

		if (isEmpty())
			return "[]";

		return toArrayList().toString();
	}

	public ArrayList<Object> toArrayList() {
		ArrayList LA = new ArrayList();
		if (root == null) {
			return LA;
		}
		root.toArrayList(LA);
		return LA;

	}

	public Iterator<T> iterator() {
		return new PreOrderIterator(this);
	}

	public Object[] toArray() {
		if (this.root == null) {
			return new Object[0];
		}
		return root.toArray();
	}

	public boolean insert(T i) {
		if (i == null) {
			throw new IllegalArgumentException();
		}
		if (isEmpty()) {
			this.root = new BinaryNode(i);
			return true;
		}

		AVLInsertHelper iHelp = new AVLInsertHelper();
		this.root = this.root.insert(i, iHelp);
		this.rotationNum += iHelp.insertNum;
		return iHelp.getStatus();
	}

	public boolean remove(T element) {

		AVLInsertHelper iHelp = new AVLInsertHelper();
		iHelp.serTrue();
		if (element == null) {
			throw new IllegalArgumentException();
		}
		if (this.root == null) {
			return false;
		}
		this.root = this.root.remove(element, iHelp);
		this.rotationNum += iHelp.insertNum;
		return iHelp.getStatus();
	}

	public class BinaryNode {
		private T element;
		private BinaryNode leftChild;
		private BinaryNode rightChild;
		private int height;

		public BinaryNode(T element) {
			this.height = 0;
			this.element = element;
			this.leftChild = null;
			this.rightChild = null;
		}

		public BinaryNode insert(T i, AVLTree<T>.AVLInsertHelper IHelp) {

			BinaryNode newNode = new BinaryNode(i);
			int val = this.element.compareTo(i);
			
			if (val < 0) {
				if (this.rightChild == null) {
					this.rightChild = newNode;
					IHelp.serTrue();
					
					if (!this.checkBalance()) {
						return rotate(IHelp);
					}
					
					return this;
				}
				this.rightChild = this.rightChild.insert(i, IHelp);
				
				
				if (!this.checkBalance()) {
					return this.rotate(IHelp);
				}
				
				return this;
			}
			
			if (val > 0) {
				if (this.leftChild == null) {
					this.leftChild = newNode;
					IHelp.serTrue();
					
					if (!this.checkBalance()) {
						return this.rotate(IHelp);
					}
					
					return this;
				}
				this.leftChild = this.leftChild.insert(i, IHelp);

				if (!this.checkBalance()) {
					return rotate(IHelp);
				}
				
				return this;
			}
			
			return this;
		}


		private BinaryNode rotate(AVLTree<T>.AVLInsertHelper iHelp) {
			

			// rotate left
			if (this.leftChild != null && (this.leftChild.leftChild != null || this.leftChild.rightChild != null)) {
				if (this.rightChild == null || this.leftChild.adjustHeight() > this.rightChild.adjustHeight()) {
					iHelp.insertOnce();
					if(!this.leftChild.hasBothChild() && this.leftChild.rightChild != null){
						iHelp.insertOnce();
						BinaryNode cur = this.leftChild;
						this.leftChild = cur.rightChild;
						this.leftChild.leftChild = cur;
						cur.rightChild = null;
					}
					BinaryNode cur = this.leftChild;
					this.leftChild = cur.rightChild;
					cur.rightChild = this;
					return cur;
				}
			}

			if (this.rightChild != null && (this.rightChild.rightChild != null || this.rightChild.leftChild != null)) {
				if (this.leftChild == null || this.rightChild.adjustHeight() > this.leftChild.adjustHeight()) {
					iHelp.insertOnce();
					if(!this.rightChild.hasBothChild() && this.rightChild.leftChild != null){
						iHelp.insertOnce();
						BinaryNode cur = this.rightChild;
						this.rightChild = cur.leftChild;
						this.rightChild.rightChild = cur;
						cur.leftChild = null;
					}
					BinaryNode cur = this.rightChild;
					this.rightChild = cur.leftChild;
					cur.leftChild = this;
					return cur;
				}
			}

			return this;
		}

		public boolean checkBalance() {

			if(this.hasBothChild()){
				int value = Math.abs(this.leftChild.adjustHeight() - this.rightChild.adjustHeight());
				if(value >= 2){
					return false;
				}
				return true;
			}
			if(this.leftChild != null || this.rightChild != null){
				if(this.adjustHeight() >= 2){
					return false;
				}
				return true;
			}
			return true;
		}

		public void setRightChild(BinaryNode NRightChild) {
			this.rightChild = NRightChild;
		}

		public void setLeftChild(BinaryNode NLeftChild) {
			this.leftChild = NLeftChild;
		}

		public BinaryNode remove(T element, AVLInsertHelper b) {

			int value = this.element.compareTo(element);

			if (value < 0) {
				if (this.rightChild != null) {
					this.rightChild = this.rightChild.remove(element, b);
					if(!this.checkBalance()){
						return this.rotate(b);
					}
				} else
					b.setFalse();

				return this;
			}
			if (value > 0) {
				if (this.leftChild != null) {
					this.leftChild = this.leftChild.remove(element, b);
					if(!this.checkBalance()){
						return this.rotate(b);
					}
				} else
					b.setFalse();

				return this;
			}

			if (this.rightChild == null && this.leftChild == null) {
				return null;
			}

			if (this.rightChild == null) {
				if(!this.checkBalance()){
					return this.rotate(b);
				}
				return this.leftChild;
			}

			if (this.leftChild == null) {
				if(!this.checkBalance()){
					return this.rotate(b);
				}
				return this.rightChild;
			}

			this.element = this.leftChild.getMax().getElement();
			this.leftChild = this.leftChild.remove(this.element, b);
			if(!this.checkBalance()){
				return this.rotate(b);
			}
			return this;

		}

		public BinaryNode getMax() {
			if (this.rightChild != null) {
				return this.rightChild.getMax();
			}
			return this;
		}
		
		public boolean hasBothChild(){
			if(this.leftChild != null && this.rightChild != null){
				return true;
			}
			return false;
		}

		// Contain => true; not contain => false
		public boolean isContain(T cur) {
			int val = this.element.compareTo(cur);
			if (this == null) {
				return false;
			}
			if (this.element.equals(cur)) {
				return true;
			}
			if (val > 0) {
				if (this.leftChild == null)
					return false;
				return this.leftChild.isContain(cur);
			}
			if (val < 0) {
				if (this.rightChild == null)
					return false;
				return this.rightChild.isContain(cur);
			}
			return false;
		}

		public T getElement() {
			return element;
		}

		public int adjustHeight() {

			int LeftHeight = -1;
			int RightHeight = -1;
			
			if(this.leftChild != null){
				LeftHeight = this.leftChild.adjustHeight();
			}
			if(this.rightChild != null){
				RightHeight = this.rightChild.adjustHeight();
			}
			
			return 1 + Math.max(LeftHeight, RightHeight);
			
		}


		public int getSize() {
			return size(this);
		}

		public int size(BinaryNode node) {
			if (node == null)
				return 0;
			return (size(node.leftChild) + 1 + size(node.rightChild));
		}

		public ArrayList<T> toArrayList(ArrayList a) {
			if (this.leftChild != null) {
				a = this.leftChild.toArrayList(a);
			}

			a.add(element);

			if (this.rightChild != null) {
				a = this.rightChild.toArrayList(a);
			}

			return a;
		}

		public Object[] toArray() {
			ArrayList<T> AL = new ArrayList<>();
			Object[] a = new Object[this.getSize()];
			AL = this.toArrayList(AL);
			for (int i = 0; i < AL.size(); i++) {
				a[i] = AL.get(i);
			}
			return a;
		}

		public BinaryNode getLeftChild() {
			return this.leftChild;
		}

		public BinaryNode getRightChild() {
			return this.rightChild;
		}
	}

	private class AVLInsertHelper {

		private int insertNum;
		private boolean insertStatus;

		public AVLInsertHelper() {
			this.insertNum = 0;
			this.insertStatus = false;
		}

		public void insertOnce() {
			this.insertNum += 1;
		}

		public void serTrue() {
			this.insertStatus = true;
		}
		
		public void setFalse(){
			this.insertStatus = false;
		}

		public boolean getStatus() {
			return this.insertStatus;
		}
	}
	

	private class PreOrderIterator implements Iterator {

		private Stack<BinaryNode> st;
		private AVLTree<T> BST;
		private BinaryNode cur;
		private int size;

		public PreOrderIterator(AVLTree<T> BST) {
			this.BST = BST;
			this.st = new Stack<BinaryNode>();
			if (this.BST.root != null) {
				st.push(this.BST.root);
				this.size = BST.size();
			}
		}

		@Override
		public boolean hasNext() {
			return !st.isEmpty();
		}

		@Override
		public T next() {
			if (st.isEmpty()) {
				throw new NoSuchElementException();
			}
			this.cur = st.pop();
			if (cur.rightChild != null) {
				st.push(cur.rightChild);
			}
			if (cur.leftChild != null) {
				st.push(cur.leftChild);
			}
			return cur.element;
		}

		public void remove() {
			if (this.size != this.BST.size())
				throw new ConcurrentModificationException();
			if (this.cur == null)
				throw new IllegalStateException();

			this.BST.remove(this.cur.element);
			if (cur.leftChild != null && cur.rightChild != null) {
				st.push(cur);
			}
			this.size = this.BST.size();
			this.cur = null;
		}
	}
}
