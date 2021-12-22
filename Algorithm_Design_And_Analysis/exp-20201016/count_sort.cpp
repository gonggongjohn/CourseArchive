#include <iostream>
#include <fstream>
#include <cstdlib>
#include <vector>
using namespace std;
int main() {
    ifstream fin("data.txt");
    clock_t start, stop;
    vector<int> a;
    int n, tmp, maxn, minn;
    maxn = -99999999;
    minn = 99999999;
    while (!fin.eof()){
        fin>>tmp;
        if(tmp > maxn) maxn = tmp;
        else if(tmp < minn) minn = tmp;
        a.push_back(tmp);
        n++;
    }
    n--;
    vector<int> c(maxn - minn + 1, 0), r(n);
    start = clock();
    for(int i = 0; i < a.size(); i++){
        c[a[i] - minn]++;
    }
    for(int i = 1; i < c.size(); i++){
        c[i] = c[i-1] + c[i];
    }
    for(int i = 0; i < a.size(); i++){
        r[c[a[i] - minn] - 1] = a[i];
        c[a[i] - minn]--;
    }
    stop = clock();
    //for(int i = 0; i < r.size(); i++) cout<<r[i]<<" ";
    //cout<<endl;
    cout<<"Total time: "<<(double) (stop - start) / CLOCKS_PER_SEC<<endl;
    fin.close();
    return 0;
}
