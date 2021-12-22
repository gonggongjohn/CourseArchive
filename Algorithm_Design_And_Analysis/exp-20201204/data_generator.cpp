#include<iostream>
#include<fstream>
#include<cstdlib>
#include<ctime>
using namespace std;
int main(){
    ofstream fout("data.txt");
    srand(time(0));
    int n;
    cin>>n;
    fout<<n<<" ";
    for(int i = 0; i < n; i++) fout<<rand() % n<<" ";
    fout.close();
    return 0;
}