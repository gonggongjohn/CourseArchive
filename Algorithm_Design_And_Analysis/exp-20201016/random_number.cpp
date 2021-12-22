#include<iostream>
#include<fstream>
#include<cstdlib>
#include<ctime>
using namespace std;
int main(){
    srand(time(0));
    ofstream fout("data.txt");
    int n, m;
    cin>>n>>m;
    for(int i = 0; i < n; i++) fout<<1 + rand() % (m - 1)<<" ";
    fout.close();
    return 0;
}