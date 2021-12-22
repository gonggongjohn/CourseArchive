#include <iostream>
#include <fstream>
#include <algorithm>
#include <set>
#include <vector>
using namespace std;
int adj[10][10] = {0}, n;
vector<set<int>> cliques;
void find_clique(set<int> r, set<int> p, set<int> x){
    if(p.empty() && x.empty()){
        cliques.push_back(r);
    }
    else if (p.empty()){
        return;
    }
    set<int> p_mut = p;
    for(int iter : p){
        set<int> r_tmp = r, p_tmp, x_tmp, adj_tmp;
        r_tmp.insert(iter);
        for (int i = 0; i < n; i++)
            if(adj[iter][i] == 1)
                adj_tmp.insert(i);
        set_intersection(begin(p_mut), end(p_mut), begin(adj_tmp), end(adj_tmp), inserter(p_tmp, p_tmp.begin()));
        set_intersection(begin(x), end(x), begin(adj_tmp), end(adj_tmp), inserter(x_tmp, x_tmp.begin()));
        find_clique(r_tmp, p_tmp, x_tmp);
        p_mut.erase(iter);
        x.insert(iter);
    }
}
int main() {
    ifstream fin("data.txt");
    set<int> r, p, x;
    fin>>n;
    while(!fin.eof()){
        int fe, te;
        fin>>fe>>te;
        adj[fe][te] = 1;
        adj[te][fe] = 1;
    }
    for(int i = 0; i < n; i++) p.insert(i);
    find_clique(r, p, x);
    unsigned int max_cs = 0;
    for(int i = 0; i < cliques.size(); i++) {
        if(cliques[i].size() > max_cs) max_cs = cliques[i].size();
    }
    for(int i = 0; i < cliques.size(); i++) {
        if(cliques[i].size() == max_cs){
            for(int iter: cliques[i]){
                cout<<iter<<" ";
            }
            cout<<endl;
        }
    }
    fin.close();
    return 0;
}
