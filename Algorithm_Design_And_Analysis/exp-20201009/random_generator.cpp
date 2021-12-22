#include <iostream>
#include <fstream>
#include <cstdlib>
#include <ctime>
using namespace std;
int main(){
    ofstream fout("data.txt");
    int n;
    srand(time(0));
    cin>>n;
    for(int i = 0; i < n; i++) fout<<rand()<<" ";
    fout.close();
    return 0;
}