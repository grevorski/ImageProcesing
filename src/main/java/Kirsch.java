////mapa
//    void GeodeticDistanceMap() throws IOException {
//            BufferedImage dziuryImg;
//            File f = new File("D:\\DEV\\GitHub\\ImageProcesing\\src\\main\\resources\\dziury.bmp");
//            dziuryImg = ImageIO.read(f);
//
//            int dziuryWidth = dziuryImg.getWidth(), dziuryHeight = dziuryImg.getHeight();
//            int x1, y1;
//            System.out.println("Based on dziury.bmp image\nEnter start coordinates (2 numbers) (260 82)\n");
//            x1 = sc.nextInt();
//            y1 = sc.nextInt();
//
//            if (x1 < 0 || y1 < 0 || x1 >= dziuryWidth || y1 >= dziuryWidth) {
//        System.out.println("Bad numbers!");
//        System.exit(1);
//        }
//
//        // create marker matrix
//        // vector<vector<int>> marker(dziuryWidth, vector<int>(dziuryHeight, 0));
//        int[][] marker = new int[dziuryWidth][dziuryHeight];
//        // Set marker initial point
//        marker[x1][y1] = 1;
//
//        // create SE
//        int[][] SE = { {1,1,1},{1,1,1},{1,1,1} };
//
//        // start iteration
//        int iter = 0, prevPixels = 1, currPixels = 0;
//
//        while (currPixels - prevPixels != 0) {
//        binaryDilation(marker, SE, iter);
//        prevPixels = currPixels;
//        currPixels = ANDmask(marker, dziuryImg);
//        ++iter;
//        }
//        // cout << "Max Distance: " << iter;
//        System.out.println("Max Distance: " + iter);
//
//        // TODO normalize
//        normalizeMarker(marker);
//
//        saveMarkerToFile(marker, dziuryWidth, dziuryHeight);
//
//        }
////vector<vector<int>>& newMarker
//        void binaryDilation(int[][] newMarker, int[][] se, int iter) {
//        //vector<vector<int>> marker;
//        int[][] marker = newMarker;
//        //std::copy(newMarker.begin(), newMarker.end(), std::back_inserter(marker));
//
//        int posX, posY;
//        Boolean flag;
//        for (int kx = 3; kx < marker.length - 3; kx++) {
//        for (int kz = 3; kz < marker[kx].length - 3; kz++) {
//        if (marker[kx][kz] == 0) {
//        flag = false;
//        for (int i = 0; i < 3; i++) {
//        posX = kx + i - 1;
//        for (int j = 0; j < 3; j++) {
//        posY = kz + j - 1;
//        if (se[i][j] == 1 && marker[posX][posY] > 0) {
//        flag = true;
//        break;
//        }
//        }
//        if (flag) break;
//        }
//        if (flag) {
//        newMarker[kx][kz] = iter;
//        }
//        }
//        }
//        }
//        }
////vector<vector<int>>& marker
//        int ANDmask(int[][] marker, BufferedImage image) {
//        int pixelCount = 0;
//        for (int kx = 3; kx < marker.length - 3; kx++) {
//        for (int kz = 3; kz < marker[kx].length - 3; kz++) {
//
//        //int imgVal = image.GetPixel(kx, kz).Red;
//        Color color = new Color(image.getRGB(kx,kz));
//        int imgVal = color.getRed();
//
//        if (imgVal == 255 && marker[kx][kz] > 0) {
//        pixelCount++;
//        }
//        else {
//        marker[kx][kz] = 0;
//        }
//        }
//        }
//        return pixelCount;
//        }
////vector<vector<int>>& marker
//        void normalizeMarker(int[][] marker) {
//        // find max and min
//        int max = 0, min = marker[0][0];
//        for (int kx = 0; kx < marker.length; kx++) {
//        for (int kz = 0; kz < marker[kx].length; kz++) {
//        if (marker[kx][kz] < min)
//        min = marker[kx][kz];
//        if (marker[kx][kz] > max)
//        max = marker[kx][kz];
//        }
//        }
//
//        // normalize
//        double fraction = (0 - 255) / (double)(min - max);
//        for (int kx = 0; kx < marker.length; kx++) {
//        for (int kz = 0; kz < marker[kx].length; kz++) {
//        marker[kx][kz] = (int) ((fraction * (marker[kx][kz] - min)) + 0);	// normalization equation
//        }
//        }
//        }
//
//        void saveMarkerToFile(int[][] marker, int width, int height) {
//        BufferedImage outputImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
//        Color Px;
//
//        for (int i = 0; i < marker.length; i++)
//        {
//        for (int j = 0; j < marker[i].length; j++)
//        {
//        if (marker[i][j] > 0) {
//        Color color = new Color(marker[i][j], marker[i][j], marker[i][j]);
//
//        outputImage.setRGB(i,j,color.getRGB());
//        // outputImage->SetPixel(i, j, Px.FromArgb(marker[i][j], marker[i][j], marker[i][j]));
//        }
//        else {
//        // outputImage->SetPixel(i, j, Px.FromArgb(0, 0, 0));
//        Color color = new Color(0, 0, 0);
//        outputImage.setRGB(i,j,color.getRGB());
//        }
//
//        }
//        }
//
//        File f = new File("geomap.png");
//        try {
//        ImageIO.write(outputImage, "png", f);
//
//        } catch (IOException e) {
//        System.out.println("Exception occured :" + e.getMessage());
//        }
//        System.out.println("Images were written succesfully.");
//
//        }
