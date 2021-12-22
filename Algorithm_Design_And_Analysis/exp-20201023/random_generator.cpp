#include <iostream>
#include <fstream>
#include <cstdlib>
#include <ctime>
using namespace std;
int main() {
    srand(time(0));
    ofstream fout("data.txt");
    int s, t, n;
    cin>>s>>t>>n;
    for(int i = 0; i < n; i++) fout<<s + rand() % (t - s)<<" ";
    fout.close();
    return 0;
}
