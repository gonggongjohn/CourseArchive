#include<iostream>
#include<fstream>
#include<algorithm>
#include<cstring>
#include<map>
using namespace std;
string a[10000005];
int e[20][20], w[20][20], root[20][20];
map<string, int> freq_table;
int cnt = 0;
struct node{
    string name;
    string description;
    struct node *lchild;
    struct node *rchild;
};
void optimal_bst(int n, string *flower){
    for(int i = 1; i <= n + 1; i++){
        e[i][i - 1] = 0;
        w[i][i - 1] = 0;
    }
    for(int l = 1; l <= n; l++){
        for(int i = 1; i <= n - l + 1; i++){
            int j = i + l - 1;
            e[i][j] = 999999;
            w[i][j] = w[i][j - 1] + freq_table[flower[j - 1]];
            for(int r = i; r <= j; r++){
                int t = e[i][r - 1] + e[r + 1][j] + w[i][j];
                if(t < e[i][j]){
                    e[i][j] = t;
                    root[i][j] = r;
                }
            }
        }
    }
}
node *build_bst(string *flower, string *description, int i, int j){
    struct node *rnode = new node();
    rnode->name = flower[root[i][j] - 1];
    rnode->description = description[root[i][j] - 1];
    if(i >= j) return rnode;
    struct node *lc = build_bst(flower, description, i, root[i][j] - 1);
    struct node *rc = build_bst(flower, description, root[i][j] + 1, j);
    rnode->lchild = lc;
    rnode->rchild = rc;
    return rnode;
}
string search(struct node *rnode,string *name){
    struct node *cur = rnode;
    while (cur){
        cnt++;
        if((*name) == cur->name) return cur->description;
        if (cur->lchild && ((*name) < cur->name)) cur = cur->lchild;
        else cur = cur->rchild;
    }
}
int main(){
    string flower[18] = {"paeoniaSA", "paeoniaLP", "camellia", "chrysanthemum", "plum", "orchid",
                         "rose", "azalea", "tulip", "jasmine", "begonia", "lotus",
                         "gardenia", "lotus", "lily", "carnation", "rose", "gesang"};
    //Simulate the description of flowers
    string description[18] = {"DpaeoniaSA", "DpaeoniaLP", "Dcamellia", "Dchrysanthemum", "Dplum", "Dorchid",
                              "Drose", "Dazalea", "Dtulip", "Djasmine", "Dbegonia", "Dlotus",
                              "Dgardenia", "Dlotus", "Dlily", "Dcarnation", "Drose", "Dgesang"};
    int freq[18] = {6, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1};
    for(int i = 0; i < 18; i++){
        freq_table[flower[i]] = freq[i];
    }
    sort(flower, flower + 18);
    sort(description, description + 18);
    ifstream fin("data.txt");
    clock_t start, stop;
    int n = 0;
    while(!fin.eof()){
        fin>>a[n];
        n++;
    }
    n--;
    optimal_bst(18, flower);
    struct node *troot = build_bst(flower, description, 1, 18);
    start = clock();
    for(int i = 0; i < n; i++){
        string t = search(troot, &a[i]);
        //cout<<t<<" ";
    }
    stop = clock();
    cout<<endl;
    cout<<"Total count: "<<cnt<<endl;
    cout<<"Total time: "<<(double)(stop - start) / CLOCKS_PER_SEC<<"s"<<endl;
    return 0;
}