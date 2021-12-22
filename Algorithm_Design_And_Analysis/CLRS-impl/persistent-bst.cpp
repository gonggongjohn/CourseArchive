#include <iostream>
#include <vector>
using namespace std;
struct node{
    int data;
    struct node *lchild;
    struct node *rchild;
};
struct node *copy_node(struct node *n){
    struct node *nnode = new node();
    (*nnode).data = n->data;
    (*nnode).lchild = n->lchild;
    (*nnode).rchild = n->rchild;
    return nnode;
}
struct node *create_node(int data){
    struct node *nnode = new node();
    (*nnode).data = data;
    return nnode;
}
struct node *persistent_insert(struct node *root, int ndata){
    struct node *nroot = copy_node(root);
    struct node *cur = nroot;
    bool finish = false;
    while (!finish){
        if (ndata <= (*cur).data){
            if (!(*cur).lchild){
                struct node *leaf = create_node(ndata);
                (*cur).lchild = leaf;
                finish = true;
            }
            else{
                struct node *nnode = copy_node((*cur).lchild);
                (*cur).lchild = nnode;
                cur = (*cur).lchild;
            }
        }
        else{
            if (!(*cur).rchild){
                struct node *leaf = create_node(ndata);
                (*cur).rchild = leaf;
                finish = true;
            }
            else{
                struct node *nnode = copy_node((*cur).rchild);
                (*cur).rchild = nnode;
                cur = (*cur).rchild;
            }
        }
    }
    return nroot;
}
vector<int> *traverse(vector<int> *result, struct node *node){
    (*result).push_back((*node).data);
    if (!(*node).lchild && !(*node).rchild) return result;
    if ((*node).lchild) {
        traverse(result, (*node).lchild);
    }
    if ((*node).rchild) {
        traverse(result, (*node).rchild);
    }
    return result;
}
int main() {
    struct node *origin_root = new node();
    (*origin_root).data = 5;
    struct node *n1root = persistent_insert(origin_root, 10);
    struct node *n2root = persistent_insert(n1root, 3);
    vector<int> r;
    traverse(&r, n2root);
    for (int i = 0; i < r.size(); ++i) {
        cout<<r[i]<<" ";
    }
    return 0;
}
