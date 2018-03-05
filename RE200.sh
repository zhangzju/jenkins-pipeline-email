echo "**************** Auto Make Timestamp touch ******************"
filelist="Makefile.in Makefile.flat Makefile aclocal.m4 conf.in configure.in *config?h.in *config.in configure config.status config.h"
for i in $filelist
do
echo " touch \"$i\""
find . -name "$i" | xargs -i{} touch -c {}
done
echo "Done"
echo
