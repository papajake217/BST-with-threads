
class Node:
    def __init__(self,name=None):
        self.name = name
        self.left = None
        self.right = None
        
    def __iter__(self):
        self.num = 0
        self.vals = self.inorder()
        
        return self
    

    def __next__(self):
        if self.num >= len(self.vals):
            raise StopIteration
        else:
            val = self.vals[self.num]
            self.num += 1
            return val

    def inorder(self):
        lst = []
        if self == None:
            return lst

        if(self.left != None):
            lst.extend(self.left.inorder())
        
        lst.append(self.name)


        if(self.right != None):
            lst.extend(self.right.inorder())

           
        return lst


class BinarySearchTree:
    def __init__(self,name,root):
        self.name = name
        self.root = root
        self.hasRoot = False
        self.sortedValues = []

    def add_all(self,*args):
        for input in args:
            self.sortedValues.append(input)
            self.sortedValues.sort()
            if not self.hasRoot:
                self.root = Node(input)
                self.hasRoot = True
            else:
                self.insertIntoBST(self.root,input)

    def insertIntoBST(self,root,node):
        if node > root.name:
            if root.right != None:
                self.insertIntoBST(root.right,node)
            else:
                root.right = Node(node)
        else:
            if root.left != None:
                self.insertIntoBST(root.left,node)
            else: 
                root.left = Node(node)

    def printTree(self,root):
        if(root != None):
            print(root.name)
        
        if(root.left != None):
            self.printTree(root.left)

        if(root.right != None):
            self.printTree(root.right)

    def __iter__(self):
        self.num = 0
        return self
    
    def __next__(self):
        if(self.num >= len(self.sortedValues)):
            raise StopIteration
        else:
            val = self.sortedValues[self.num]
            self.num += 1
            return val

    def __str__(self):
        string = ("[" + str(self.name) + "] ")
        return string + self.preorder(self.root)
        

    def preorder(self,root):
        if(root != None):
            string = str(root.name) 
        
        if(root.left != None):
            string += " L:(" + self.preorder(root.left) + ")"

        if(root.right != None):
            string += " R:(" + self.preorder(root.right) + ")"
        return string


if __name__ == "__main__":
    tree = BinarySearchTree(name="Oak", root=Node())
    tree.add_all(5, 3, 9, 0) # adds the elements in the order 5, 3, 9, and then 0
    print(tree)
    t1 = BinarySearchTree(name="Oak", root=Node())
    t2 = BinarySearchTree(name="Birch", root=Node())
    t1.add_all(5, 3, 9, 0)
    t2.add_all(1, 0, 10, 2, 7)
    for x in t1.root:
        print(x)
    for x in t2.root:
         print(x)
