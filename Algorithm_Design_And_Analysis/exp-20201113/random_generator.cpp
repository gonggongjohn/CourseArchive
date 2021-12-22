#include <iostream>
#include <fstream>
#include <cstdlib>
#include <ctime>
using namespace std;
int main() {
    ofstream fout("data.txt");
    srand(time(0));
    int s, t, n;
    cin>>s>>t>>n;
    for(int i = 0; i < n; i++) fout<<s + rand() % (t - s + 1)<<" ";
    fout.close();
    return 0;
}
